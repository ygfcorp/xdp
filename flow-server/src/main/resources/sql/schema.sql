drop table if exists flow;
create table flow
(
    id                          varchar(100)  not null primary key,
    client_id                   bigint        not null default 0,
    name                        varchar(100)  not null,
    source_type                 varchar(50)   not null,
    source_connection_info      varchar(1000) not null,
    source_table                varchar(1000) not null,
    source_columns              varchar(1000) not null,
    destination_type            varchar(50)   not null,
    destination_connection_info varchar(1000) not null,
    nifi_pg_id                  varchar(100)           default null,
    status                      varchar(20)   not null default 'REGISTERED',
    created_at                  timestamp     not null,
    updated_at                  timestamp     not null,
    key clientid_updatedat (client_id, updated_at),
    unique nifipgid (nifi_pg_id),
    key status (status)
);

drop table if exists flow_history;
create table flow_history
(
    id                          bigint        not null auto_increment primary key,
    flow_id                     varchar(100)  not null,
    name                        varchar(100)  not null,
    source_type                 varchar(50)   not null,
    source_connection_info      varchar(1000) not null,
    source_table                varchar(1000) not null,
    source_columns              varchar(1000) not null,
    destination_type            varchar(50)   not null,
    destination_connection_info varchar(1000) not null,
    nifi_pg_id                  varchar(100)           default null,
    active                      boolean       not null default false,
    created_at                  timestamp     not null,
    updated_at                  timestamp     not null,
    key flowid (flow_id)
);

drop table if exists recipe;
create table recipe
(
    id          varchar(100) not null primary key,
    name        varchar(100) not null,
    status      varchar(20)  not null default 'REGISTERED',
    dag         json,
    description mediumtext,
    created_at  timestamp    not null,
    updated_at  timestamp    not null,
    key (status, updated_at)
);

drop table if exists processor;
create table processor
(
    id                varchar(100) not null primary key,
    name              varchar(100) not null,
    nifi_processor_id varchar(100) not null,
    status            varchar(20)  not null default 'REGISTERED',
    created_at        timestamp    not null,
    updated_at        timestamp    not null,
    key (status, updated_at)
);
