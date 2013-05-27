package jobs;

import models.Notification;
import models.Order;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import util.ConfigReader;
import util.Mailer;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: flukito
 * Date: 8/9/12
 * Time: 11:54 PM
 * To change this template use File | Settings | File Templates.
 */

public class SendNotificationsJob {

    public SendNotificationsJob() {
        Akka.system().scheduler().schedule(
                Duration.create(0, TimeUnit.MILLISECONDS),
                Duration.create(1, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        List<Notification> pendingRecipientNotificationList = Notification.getPendingNotifications();
                        for(Notification pendingRecipientNotification: pendingRecipientNotificationList){
                            if(pendingRecipientNotification.order!=null){
                                Order tmpOrder = pendingRecipientNotification.order;

                                if(tmpOrder.orderStatus != Order.ORDER_STATUS.PENDING_PAYMENT){
                                    if(tmpOrder.orderStatus == Order.ORDER_STATUS.PENDING_RECIPIENT) {
                                        if(!tmpOrder.senderEmail.equalsIgnoreCase(tmpOrder.recipientEmail)) {
                                            Mailer.sendOrderNotification(tmpOrder);
                                            Mailer.sendGiftNotification(tmpOrder);

                                        }

                                    }else if(tmpOrder.orderStatus == Order.ORDER_STATUS.CONFIRMED) {
                                        if(tmpOrder.senderEmail.equals(tmpOrder.recipientEmail)) {
                                            Mailer.sendSelfOrderNotification(tmpOrder);

                                        }

                                    }
                                    pendingRecipientNotification.status="Completed";
                                    pendingRecipientNotification.update();
                                }
                            }
                        }
                    }
                },
                Akka.system().dispatcher()
        );

    }

}
