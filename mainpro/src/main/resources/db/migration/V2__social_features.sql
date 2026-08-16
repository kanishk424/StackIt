CREATE TABLE `follows` (
                           `follower_id` BIGINT NOT NULL,
                           `following_id` BIGINT NOT NULL,
                           PRIMARY KEY (`follower_id`, `following_id`),
                           CONSTRAINT `fk_follows_follower`
                               FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`)
                                   ON DELETE CASCADE,
                           CONSTRAINT `fk_follows_following`
                               FOREIGN KEY (`following_id`) REFERENCES `users` (`id`)
                                   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
