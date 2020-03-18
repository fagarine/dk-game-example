CREATE TABLE `id_incrementer`
(
    `user_id` bigint(20) NOT NULL DEFAULT '0',
    `role_id` bigint(20) NOT NULL DEFAULT '0'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='自增id表';
INSERT INTO id_incrementer
VALUES (0, 0);

CREATE TABLE `p_account`
(
    `account_id`  int(10)                                                       NOT NULL AUTO_INCREMENT,
    `plat_no`     int(10)                                                       NOT NULL COMMENT '玩家来源渠道号',
    `child_no`    int(10)                                                       NOT NULL DEFAULT '0' COMMENT '如果是聚合渠道，记录子渠道号',
    `plat_id`     varchar(127) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '玩家在来源渠道的平台id',
    `device_no`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '玩家最后登录的设备号',
    `server_id`   int(10)                                                       NOT NULL DEFAULT '0' COMMENT '记录玩家最后登录的服务器id',
    `role_id`     bigint(20)                                                    NOT NULL DEFAULT '0' COMMENT '如果玩家创建了角色，记录角色id',
    `white_name`  int(3)                                                        NOT NULL DEFAULT '0' COMMENT '玩家所属白名单类型',
    `forbid`      int(3)                                                        NOT NULL DEFAULT '0' COMMENT '记录玩家是否被禁，如果被禁，记录被禁类型',
    `lift_time`   datetime                                                               DEFAULT NULL COMMENT '如果被禁，记录解禁时间',
    `create_time` datetime                                                      NOT NULL COMMENT '帐号创建时间',
    `login_time`  datetime                                                      NOT NULL COMMENT '记录最后一次登录时间',
    PRIMARY KEY (`account_id`),
    UNIQUE KEY `IDX_ACCOUNT_DEVICE_NO` (`device_no`),
    UNIQUE KEY `IDX_ACCOUNT_ROLE_ID` (`role_id`),
    UNIQUE KEY `IDX_ACCOUNT_PLAT_ID` (`plat_no`, `child_no`, `plat_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `p_role`
(
    `role_id`     bigint(20)                             NOT NULL,
    `nick`        varchar(40) COLLATE utf8mb4_general_ci NOT NULL,
    `portrait`    int(10)                                NOT NULL DEFAULT '0',
    `role_lv`     int(10)                                NOT NULL DEFAULT '1',
    `exp`         int(10)                                NOT NULL DEFAULT '0',
    `vip`         int(5)                                 NOT NULL DEFAULT '0',
    `recharge`    int(10)                                NOT NULL DEFAULT '0' COMMENT '记录到分',
    `gold`        int(10)                                NOT NULL DEFAULT '0',
    `gold_cost`   int(10)                                NOT NULL DEFAULT '0',
    `gold_give`   int(10)                                NOT NULL DEFAULT '0',
    `gold_time`   datetime                                        DEFAULT NULL,
    `role_state`  int(5)                                 NOT NULL DEFAULT '0',
    `on_time`     datetime                                        DEFAULT NULL,
    `ol_time`     int(10)                                NOT NULL DEFAULT '0',
    `off_time`    datetime                                        DEFAULT NULL,
    `create_time` datetime                               NOT NULL,
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `IDX_ROLE_NICK` (`nick`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE `s_bullet`
(
    `bullet_id`    int(10)                                                       NOT NULL,
    `bullet_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `bullet_type`  int(10)                                                       NOT NULL,
    `distance`     int(10)                                                       NOT NULL,
    `speed`        int(10)                                                       NOT NULL,
    `blast_radius` int(10)                                                       NOT NULL DEFAULT '0',
    `cool_time`    int(10)                                                       NOT NULL DEFAULT '0' COMMENT '毫秒',
    `width`        int(5)                                                        NOT NULL DEFAULT '0',
    `height`       int(5)                                                        NOT NULL DEFAULT '0',
    `img_width`    int(5)                                                        NOT NULL DEFAULT '0',
    `img_height`   int(5)                                                        NOT NULL DEFAULT '0',
    PRIMARY KEY (`bullet_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='子弹配置表';
INSERT INTO `s_bullet`(`bullet_id`, `bullet_name`, `bullet_type`, `distance`, `speed`, `blast_radius`, `cool_time`, `width`, `height`, `img_width`, `img_height`)
values (1, '初始子弹', 1, 1000, 900, 0, 600, 23, 56, 70, 70);

CREATE TABLE `s_tank`
(
    `tank_id`     int(10)                                                      NOT NULL,
    `tank_name`   varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `radius`      int(10)                                                      NOT NULL,
    `half_width`  int(5)                                                       NOT NULL DEFAULT '0',
    `half_height` int(5)                                                       NOT NULL DEFAULT '0',
    PRIMARY KEY (`tank_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='坦克配置表';
INSERT INTO `s_tank`(`tank_id`, `tank_name`, `radius`, `half_width`, `half_height`)
values (101, '初始坦克', 50, 50, 50);
