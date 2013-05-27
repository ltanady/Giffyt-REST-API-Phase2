# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table GIFFYT_CAMPAIGN (
  id                        bigint auto_increment not null,
  country_id                bigint,
  name                      varchar(255),
  description               varchar(255),
  campaign_limit            bigint,
  start_date                datetime,
  end_date                  datetime,
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_CAMPAIGN primary key (id))
;

create table GIFFYT_COUNTRY (
  id                        bigint auto_increment not null,
  code                      varchar(255),
  name                      varchar(255),
  image_url                 varchar(255),
  currency                  varchar(255),
  minimum_purchase_amount   double,
  subsidized_shipping_amount double,
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  constraint uq_GIFFYT_COUNTRY_code unique (code),
  constraint uq_GIFFYT_COUNTRY_name unique (name),
  constraint pk_GIFFYT_COUNTRY primary key (id))
;

create table GIFFYT_DELIVERY_AREA_OPTION (
  id                        bigint auto_increment not null,
  area                      varchar(255),
  amount                    double,
  merchant_id               bigint,
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_DELIVERY_AREA_OPTION primary key (id))
;

create table GIFFYT_DELIVERY_DETAIL (
  id                        bigint auto_increment not null,
  recipient_name            varchar(255),
  address                   varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  country_id                bigint,
  postal_code               varchar(255),
  contact_number            varchar(255),
  shipment_date             datetime,
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_DELIVERY_DETAIL primary key (id))
;

create table GIFFYT_DELIVERY_TIME_OPTION (
  id                        bigint auto_increment not null,
  description               varchar(255),
  amount                    double,
  merchant_id               bigint,
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_DELIVERY_TIME_OPTION primary key (id))
;

create table GIFFYT_MERCHANT (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  type                      integer,
  address_1                 varchar(255),
  address_2                 varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  country_id                bigint,
  postal_code               varchar(255),
  contact_person            varchar(255),
  phone                     varchar(255),
  fax                       varchar(255),
  email                     varchar(255),
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  constraint ck_GIFFYT_MERCHANT_type check (type in (0,1)),
  constraint uq_GIFFYT_MERCHANT_1 unique (name,country_id),
  constraint pk_GIFFYT_MERCHANT primary key (id))
;

create table GIFFYT_NOTIFICATION (
  id                        bigint auto_increment not null,
  order_id                  bigint,
  status                    varchar(255),
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_NOTIFICATION primary key (id))
;

create table GIFFYT_ORDER (
  id                        bigint auto_increment not null,
  external_reference_id     bigint,
  message                   varchar(255),
  old_product_id            bigint,
  product_id                bigint,
  sender_facebook_id        varchar(255),
  sender_name               varchar(255),
  sender_email              varchar(255),
  recipient_facebook_id     varchar(255),
  recipient_name            varchar(255),
  recipient_email           varchar(255),
  preferred_notification_date datetime,
  preferred_delivery_date   datetime,
  preferred_delivery_time   varchar(255),
  delivery_area_option_id   bigint,
  delivery_time_option_id   bigint,
  delivery_detail_id        bigint,
  temporaryToken            varchar(255),
  is_surprise               tinyint(1) default 0,
  initial_amount            double,
  final_amount              double,
  order_status              integer,
  paypal_token              varchar(255),
  paypal_payer_id           varchar(255),
  paypal_authorization_id   varchar(255),
  shipment_date             datetime,
  created                   datetime,
  last_update               datetime,
  constraint ck_GIFFYT_ORDER_order_status check (order_status in (0,1,2,3,4,5,6,7)),
  constraint pk_GIFFYT_ORDER primary key (id))
;

create table GIFFYT_PRODUCT (
  type                      varchar(31) not null,
  id                        bigint auto_increment not null,
  code                      varchar(255),
  price                     double,
  brand                     varchar(255),
  merchant_id               bigint,
  campaign_id               bigint,
  exchangeable              tinyint(1) default 0,
  minimum_order_days        bigint,
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  name                      varchar(255),
  sku                       varchar(255),
  description               varchar(255),
  estimated_delivery        varchar(255),
  additional_shipping_cost  double,
  constraint pk_GIFFYT_PRODUCT primary key (id))
;

create table GIFFYT_PRODUCT_ATTRIBUTE (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  value                     varchar(255),
  product_id                bigint,
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_PRODUCT_ATTRIBUTE primary key (id))
;

create table GIFFYT_PRODUCT_IMAGE (
  id                        bigint auto_increment not null,
  image_url                 varchar(255),
  product_id                bigint,
  number                    bigint,
  is_active                 tinyint(1) default 0,
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_PRODUCT_IMAGE primary key (id))
;

create table GIFFYT_USER (
  id                        bigint auto_increment not null,
  uid                       varchar(255),
  name                      varchar(255),
  email                     varchar(255),
  sex                       varchar(255),
  birthday                  varchar(255),
  birthday_date             varchar(255),
  created                   datetime,
  last_update               datetime,
  constraint pk_GIFFYT_USER primary key (id))
;


create table GIFFYT_CAMPAIGN_USER (
  campaign_id                    bigint not null,
  facebook_user_id               bigint not null,
  constraint pk_GIFFYT_CAMPAIGN_USER primary key (campaign_id, facebook_user_id))
;
alter table GIFFYT_CAMPAIGN add constraint fk_GIFFYT_CAMPAIGN_country_1 foreign key (country_id) references GIFFYT_COUNTRY (id) on delete restrict on update restrict;
create index ix_GIFFYT_CAMPAIGN_country_1 on GIFFYT_CAMPAIGN (country_id);
alter table GIFFYT_DELIVERY_AREA_OPTION add constraint fk_GIFFYT_DELIVERY_AREA_OPTION_merchant_2 foreign key (merchant_id) references GIFFYT_MERCHANT (id) on delete restrict on update restrict;
create index ix_GIFFYT_DELIVERY_AREA_OPTION_merchant_2 on GIFFYT_DELIVERY_AREA_OPTION (merchant_id);
alter table GIFFYT_DELIVERY_DETAIL add constraint fk_GIFFYT_DELIVERY_DETAIL_country_3 foreign key (country_id) references GIFFYT_COUNTRY (id) on delete restrict on update restrict;
create index ix_GIFFYT_DELIVERY_DETAIL_country_3 on GIFFYT_DELIVERY_DETAIL (country_id);
alter table GIFFYT_DELIVERY_TIME_OPTION add constraint fk_GIFFYT_DELIVERY_TIME_OPTION_merchant_4 foreign key (merchant_id) references GIFFYT_MERCHANT (id) on delete restrict on update restrict;
create index ix_GIFFYT_DELIVERY_TIME_OPTION_merchant_4 on GIFFYT_DELIVERY_TIME_OPTION (merchant_id);
alter table GIFFYT_MERCHANT add constraint fk_GIFFYT_MERCHANT_country_5 foreign key (country_id) references GIFFYT_COUNTRY (id) on delete restrict on update restrict;
create index ix_GIFFYT_MERCHANT_country_5 on GIFFYT_MERCHANT (country_id);
alter table GIFFYT_NOTIFICATION add constraint fk_GIFFYT_NOTIFICATION_order_6 foreign key (order_id) references GIFFYT_ORDER (id) on delete restrict on update restrict;
create index ix_GIFFYT_NOTIFICATION_order_6 on GIFFYT_NOTIFICATION (order_id);
alter table GIFFYT_ORDER add constraint fk_GIFFYT_ORDER_oldProduct_7 foreign key (old_product_id) references GIFFYT_PRODUCT (id) on delete restrict on update restrict;
create index ix_GIFFYT_ORDER_oldProduct_7 on GIFFYT_ORDER (old_product_id);
alter table GIFFYT_ORDER add constraint fk_GIFFYT_ORDER_product_8 foreign key (product_id) references GIFFYT_PRODUCT (id) on delete restrict on update restrict;
create index ix_GIFFYT_ORDER_product_8 on GIFFYT_ORDER (product_id);
alter table GIFFYT_ORDER add constraint fk_GIFFYT_ORDER_deliveryAreaOption_9 foreign key (delivery_area_option_id) references GIFFYT_DELIVERY_AREA_OPTION (id) on delete restrict on update restrict;
create index ix_GIFFYT_ORDER_deliveryAreaOption_9 on GIFFYT_ORDER (delivery_area_option_id);
alter table GIFFYT_ORDER add constraint fk_GIFFYT_ORDER_deliveryTimeOption_10 foreign key (delivery_time_option_id) references GIFFYT_DELIVERY_TIME_OPTION (id) on delete restrict on update restrict;
create index ix_GIFFYT_ORDER_deliveryTimeOption_10 on GIFFYT_ORDER (delivery_time_option_id);
alter table GIFFYT_ORDER add constraint fk_GIFFYT_ORDER_deliveryDetail_11 foreign key (delivery_detail_id) references GIFFYT_DELIVERY_DETAIL (id) on delete restrict on update restrict;
create index ix_GIFFYT_ORDER_deliveryDetail_11 on GIFFYT_ORDER (delivery_detail_id);
alter table GIFFYT_PRODUCT add constraint fk_GIFFYT_PRODUCT_merchant_12 foreign key (merchant_id) references GIFFYT_MERCHANT (id) on delete restrict on update restrict;
create index ix_GIFFYT_PRODUCT_merchant_12 on GIFFYT_PRODUCT (merchant_id);
alter table GIFFYT_PRODUCT add constraint fk_GIFFYT_PRODUCT_campaign_13 foreign key (campaign_id) references GIFFYT_CAMPAIGN (id) on delete restrict on update restrict;
create index ix_GIFFYT_PRODUCT_campaign_13 on GIFFYT_PRODUCT (campaign_id);
alter table GIFFYT_PRODUCT_ATTRIBUTE add constraint fk_GIFFYT_PRODUCT_ATTRIBUTE_product_14 foreign key (product_id) references GIFFYT_PRODUCT (id) on delete restrict on update restrict;
create index ix_GIFFYT_PRODUCT_ATTRIBUTE_product_14 on GIFFYT_PRODUCT_ATTRIBUTE (product_id);
alter table GIFFYT_PRODUCT_IMAGE add constraint fk_GIFFYT_PRODUCT_IMAGE_product_15 foreign key (product_id) references GIFFYT_PRODUCT (id) on delete restrict on update restrict;
create index ix_GIFFYT_PRODUCT_IMAGE_product_15 on GIFFYT_PRODUCT_IMAGE (product_id);



alter table GIFFYT_CAMPAIGN_USER add constraint fk_GIFFYT_CAMPAIGN_USER_GIFFYT_CAMPAIGN_01 foreign key (campaign_id) references GIFFYT_CAMPAIGN (id) on delete restrict on update restrict;

alter table GIFFYT_CAMPAIGN_USER add constraint fk_GIFFYT_CAMPAIGN_USER_GIFFYT_USER_02 foreign key (facebook_user_id) references GIFFYT_USER (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table GIFFYT_CAMPAIGN;

drop table GIFFYT_CAMPAIGN_USER;

drop table GIFFYT_COUNTRY;

drop table GIFFYT_DELIVERY_AREA_OPTION;

drop table GIFFYT_DELIVERY_DETAIL;

drop table GIFFYT_DELIVERY_TIME_OPTION;

drop table GIFFYT_MERCHANT;

drop table GIFFYT_NOTIFICATION;

drop table GIFFYT_ORDER;

drop table GIFFYT_PRODUCT;

drop table GIFFYT_PRODUCT_ATTRIBUTE;

drop table GIFFYT_PRODUCT_IMAGE;

drop table GIFFYT_USER;

SET FOREIGN_KEY_CHECKS=1;

