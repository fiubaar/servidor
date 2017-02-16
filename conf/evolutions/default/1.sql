# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table project (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  user_id                   bigint,
  description               varchar(255),
  qr_file_name              varchar(255),
  model_name                varchar(255),
  logo_filename             varchar(255),
  date_creation             datetime,
  constraint uq_project_name unique (name),
  constraint pk_project primary key (id))
;

create table token (
  token                     varchar(255) not null,
  user_id                   bigint,
  type                      varchar(8),
  date_creation             datetime,
  email                     varchar(255),
  constraint ck_token_type check (type in ('password','email')),
  constraint pk_token primary key (token))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  fullname                  varchar(255),
  confirmation_token        varchar(255),
  password_hash             varchar(255),
  date_creation             datetime,
  validated                 tinyint(1) default 0,
  constraint uq_user_email unique (email),
  constraint uq_user_fullname unique (fullname),
  constraint pk_user primary key (id))
;

alter table project add constraint fk_project_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_project_user_1 on project (user_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table project;

drop table token;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

