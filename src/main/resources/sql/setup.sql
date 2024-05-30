create table if not exists jellyfin_webhook_entity
(
    uuid                  varchar(255)         not null
        primary key,
    server_name           varchar(255)         null,
    notification_type     varchar(255)         null,
    timestamp             timestamp            null,
    name                  varchar(255)         null,
    overview              varchar(1000)         null,
    item_type             varchar(255)         null,
    year                  int                  null,
    series_name           varchar(255)         null,
    season_number         int                  null,
    episode_number        int                  null,
    season_episode        varchar(255)         null,
    video0_title          varchar(255)         null,
    notification_username varchar(50)          null comment '相关用户',
    user_id               varchar(255)         null comment '相关用户',
    processed             tinyint(1) default 0 null comment '是否被处理过',
    item_id               varchar(255)         not null comment 'itemId'
)
    comment 'jellyfin webhook 持久化';

create table if not exists remote_server_info
(
    uuid         varchar(128) not null
        primary key,
    created      timestamp    null,
    modified     timestamp    null,
    access_token varchar(512) not null,
    app_id       varchar(128) not null,
    server_url   varchar(128) null comment '服务地址'
)
    charset = utf8mb3;