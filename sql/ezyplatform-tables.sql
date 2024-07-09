/*
 * Copyright 2022 youngmonkeys.org
 *
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

CREATE TABLE IF NOT EXISTS `ezy_settings` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `setting_name` varchar(120) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '',
    `data_type` varchar(12) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'STRING',
    `setting_value` varchar(2048) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_setting_name` (`setting_name`),
    INDEX `index_data_type` (`data_type`),
    INDEX `index_created_at` (`created_at`),
    INDEX `index_updated_at` (`updated_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_run_script_histories` (
    `module_name` varchar(120) NOT NULL,
    `module_type` varchar(15) NOT NULL,
    `script_name` varchar(120) NOT NULL,
    `run_at` datetime NOT NULL,
    PRIMARY KEY (`module_name`, `module_type`, `script_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_admins` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `uuid` varchar(128) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `username` varchar(64) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `email` varchar(120) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `phone` varchar(20) COLLATE utf8mb4_unicode_520_ci,
    `password` varchar(128) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `display_name` varchar(64) COLLATE utf8mb4_unicode_520_ci,
    `url` varchar(100) COLLATE utf8mb4_unicode_520_ci,
    `avatar_image_id` bigint,
    `cover_image_id` bigint,
    `status` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'ACTIVATED',
    `created_at` datetime NOT NULL DEFAULT '2021-10-27 00:00:00',
    `updated_at` datetime NOT NULL DEFAULT '2021-10-27 00:00:00',
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_uuid` (`uuid`),
    UNIQUE KEY `key_username` (`username`),
    UNIQUE KEY `key_email` (`email`),
    UNIQUE KEY `key_phone` (`phone`),
    INDEX `index_status` (`status`),
    INDEX `index_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_admin_meta` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `admin_id` bigint unsigned NOT NULL DEFAULT 0,
    `meta_key` varchar(255) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
    `meta_value` varchar(500) COLLATE utf8mb4_unicode_520_ci,
    `meta_number_value` bigint NOT NULL default 0,
    PRIMARY KEY (`id`),
    INDEX `index_key_admin_id` (`admin_id`),
    INDEX `index_meta_key` (`meta_key`),
    INDEX `index_meta_value` (`meta_value`),
    INDEX `index_meta_number_value` (`meta_number_value`),
    INDEX `index_meta_key_value` (`meta_key`, `meta_value`),
    INDEX `index_meta_key_number_value` (`meta_key`, `meta_number_value`),
    INDEX `index_admin_id_meta_key` (`admin_id`, `meta_key`),
    INDEX `index_admin_id_meta_key_value` (`admin_id`, `meta_key`, `meta_value`),
    INDEX `index_admin_id_meta_key_number_value` (`admin_id`, `meta_key`, `meta_number_value`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_admin_access_tokens` (
    `id` varchar(256) NOT NULL,
    `admin_id` bigint NOT NULL,
    `renewal_count` bigint NOT NULL,
    `status` varchar(25) COLLATE utf8mb4_unicode_520_ci,
    `created_at` datetime NOT NULL,
    `expired_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `index_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_admin_role_names` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(45) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `display_name` varchar(60) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `priority` int NOT NULL DEFAULT 0,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_name` (`name`),
    UNIQUE KEY `key_display_name` (`display_name`),
    INDEX `index_priority` (`priority`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_admin_roles` (
    `role_id` bigint unsigned NOT NULL,
    `admin_id` bigint unsigned NOT NULL,
    `created_at` datetime NOT NULL,
    PRIMARY KEY (`role_id`, `admin_id`),
    INDEX `index_role_id` (`role_id`),
    INDEX `index_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_role_features` (
    `role_id` bigint unsigned NOT NULL,
    `target` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `feature` varchar(120) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `feature_uri` varchar(300) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `feature_method` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `created_at` datetime NOT NULL,
    PRIMARY KEY (`role_id`, `target`, `feature`, `feature_uri`, `feature_method`),
    INDEX `index_role_id` (`role_id`),
    INDEX `index_role_id_target` (`role_id`, `target`),
    INDEX `index_feature_uri_method` (`target`, `feature_uri`, `feature_method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_medias` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(150) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `url` varchar(300) COLLATE utf8mb4_unicode_520_ci,
    `original_name` varchar(150) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `upload_from` varchar(9) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `media_type` varchar(9) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `mime_type` varchar(90) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `owner_user_id` bigint unsigned NOT NULL,
    `owner_admin_id` bigint unsigned NOT NULL,
    `title` varchar(100) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `caption` varchar(100) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `alternative_text` varchar(100) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `description` varchar(3500) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `public_media` tinyint NOT NULL DEFAULT 1,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_name` (`name`),
    INDEX `index_upload_from` (`upload_from`),
    INDEX `index_media_type` (`media_type`),
    INDEX `index_mime_type` (`mime_type`),
    INDEX `index_owner_user_id` (`owner_user_id`),
    INDEX `index_owner_admin_id` (`owner_admin_id`),
    INDEX `index_owner_public_media` (`public_media`),
    INDEX `index_created_at` (`created_at`),
    INDEX `index_updated_at` (`updated_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_users` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `uuid` varchar(128) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `username` varchar(128) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `display_name` varchar(250) COLLATE utf8mb4_unicode_520_ci,
    `password` varchar(255) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `email` varchar(120) COLLATE utf8mb4_unicode_520_ci,
    `phone` varchar(20) COLLATE utf8mb4_unicode_520_ci,
    `url` varchar(120) COLLATE utf8mb4_unicode_520_ci,
    `avatar_image_id` bigint,
    `cover_image_id` bigint,
    `activation_key` varchar(255) COLLATE utf8mb4_unicode_520_ci,
    `status` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'INACTIVATED',
    `created_at` datetime NOT NULL DEFAULT '2021-01-01 00:00:00',
    `updated_at` datetime NOT NULL DEFAULT '2021-01-01 00:00:00',
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_uuid` (`uuid`),
    UNIQUE KEY `key_username` (`username`),
    UNIQUE KEY `key_email` (`email`),
    UNIQUE KEY `key_phone` (`phone`),
    UNIQUE KEY `key_activation_key` (`activation_key`),
    INDEX `index_status` (`status`),
    INDEX `index_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_user_meta` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `user_id` bigint unsigned NOT NULL DEFAULT 0,
    `meta_key` varchar(255) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
    `meta_value` varchar(500) COLLATE utf8mb4_unicode_520_ci,
    `meta_number_value` bigint NOT NULL default 0,
    PRIMARY KEY (`id`),
    INDEX `index_key_user_id` (`user_id`),
    INDEX `index_meta_key` (`meta_key`),
    INDEX `index_meta_value` (`meta_value`),
    INDEX `index_meta_number_value` (`meta_number_value`),
    INDEX `index_meta_key_value` (`meta_key`, `meta_value`),
    INDEX `index_meta_key_number_value` (`meta_key`, `meta_number_value`),
    INDEX `index_user_id_meta_key` (`user_id`, `meta_key`),
    INDEX `index_user_id_meta_key_value` (`user_id`, `meta_key`, `meta_value`),
    INDEX `index_user_id_meta_key_number_value` (`user_id`, `meta_key`, `meta_number_value`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_user_keywords` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `user_id` bigint unsigned NOT NULL,
	`keyword` varchar(120) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `priority` int unsigned NOT NULL,
    `created_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_user_id_keyword` (`user_id`, `keyword`),
    INDEX `index_user_id` (`user_id`),
    INDEX `index_keyword` (`keyword`),
    INDEX `index_priority` (`priority`),
    INDEX `index_user_id_keyword_priority` (`user_id`, `keyword`, `priority`),
    INDEX `index_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_user_access_tokens` (
    `id` varchar(256) NOT NULL,
    `user_id` bigint NOT NULL,
    `renewal_count` bigint NOT NULL,
    `status` varchar(25) COLLATE utf8mb4_unicode_520_ci,
    `created_at` datetime NOT NULL,
    `expired_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `index_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_user_role_names` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(45) NOT NULL COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `display_name` varchar(60) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `priority` int NOT NULL DEFAULT 0,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_name` (`name`),
    UNIQUE KEY `key_display_name` (`display_name`),
    INDEX `index_priority` (`priority`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_user_roles` (
    `role_id` bigint unsigned NOT NULL,
    `user_id` bigint unsigned NOT NULL,
    `created_at` datetime NOT NULL,
    PRIMARY KEY (`role_id`, `user_id`),
    INDEX `index_role_id` (`role_id`),
    INDEX `index_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_admin_projects` (
    `project_name` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `version_name` varchar(12) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `updated_by_admin_id` bigint unsigned NOT NULL,
    `updated_at` datetime NOT NULL,
    PRIMARY KEY (`project_name`),
    INDEX `index_version_name` (`version_name`),
    INDEX `index_updated_by_admin_id` (`updated_by_admin_id`),
    INDEX `index_updated_at` (`updated_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_letters` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `letter_type` varchar(25) COLLATE utf8mb4_unicode_520_ci,
  `title` varchar(1200) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `content` varchar(12000) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `from_admin_id` bigint unsigned,
  `from_user_id` bigint unsigned,
  `parent_id` bigint unsigned DEFAULT 0,
  `status` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'DRAFT',
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `index_letter_type` (`letter_type`),
  INDEX `index_from_admin_id` (`from_admin_id`),
  INDEX `index_from_user_id` (`from_user_id`),
  INDEX `index_status` (`status`),
  INDEX `index_parent_id` (`parent_id`),
  INDEX `index_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_letter_receivers` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `letter_id` bigint unsigned NOT NULL,
  `to_admin_id` bigint unsigned,
  `to_user_id` bigint unsigned,
  `notification_receiver_id` bigint unsigned NOT NULL DEFAULT 0,
  `confidence_level` varchar(25) COLLATE utf8mb4_unicode_520_ci,
  `important_level` varchar(25) COLLATE utf8mb4_unicode_520_ci,
  `status` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'SENT',
  `sent_at` datetime NOT NULL,
  `received_at` datetime,
  `read_at` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_letter_to_admin_id` (`letter_id`, `to_admin_id`),
  UNIQUE KEY `key_letter_to_user_id` (`letter_id`, `to_user_id`),
  INDEX `index_letter_id` (`letter_id`),
  INDEX `index_to_admin_id` (`to_admin_id`),
  INDEX `index_to_user_id` (`to_user_id`),
  INDEX `index_notification_receiver_id` (`notification_receiver_id`),
  INDEX `index_confidence_level` (`confidence_level`),
  INDEX `index_important_level` (`important_level`),
  INDEX `index_status` (`status`),
  INDEX `index_sent_at` (`sent_at`),
  INDEX `index_received_at` (`received_at`),
  INDEX `index_read_at` (`read_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_notifications` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `notification_type` varchar(25) COLLATE utf8mb4_unicode_520_ci,
  `title` varchar(300) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `content` varchar(3000) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `deep_link` varchar(300) COLLATE utf8mb4_unicode_520_ci,
  `icon_image` varchar(120) COLLATE utf8mb4_unicode_520_ci,
  `from_admin_id` bigint unsigned,
  `from_user_id` bigint unsigned,
  `status` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'DRAFT',
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `index_notification_type` (`notification_type`),
  INDEX `index_from_admin_id` (`from_admin_id`),
  INDEX `index_from_user_id` (`from_user_id`),
  INDEX `index_status` (`status`),
  INDEX `index_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_notification_receivers` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `notification_id` bigint unsigned NOT NULL,
  `to_admin_id` bigint unsigned,
  `to_user_id` bigint unsigned,
  `confidence_level` varchar(25) COLLATE utf8mb4_unicode_520_ci,
  `important_level` varchar(25) COLLATE utf8mb4_unicode_520_ci,
  `status` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'SENT',
  `sent_at` datetime NOT NULL,
  `received_at` datetime,
  `read_at` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_notification_to_admin_id` (`notification_id`, `to_admin_id`),
  UNIQUE KEY `key_notification_to_user_id` (`notification_id`, `to_user_id`),
  INDEX `index_notification_id` (`notification_id`),
  INDEX `index_to_admin_id` (`to_admin_id`),
  INDEX `index_to_user_id` (`to_user_id`),
  INDEX `index_confidence_level` (`confidence_level`),
  INDEX `index_important_level` (`important_level`),
  INDEX `index_status` (`status`),
  INDEX `index_sent_at` (`sent_at`),
  INDEX `index_received_at` (`received_at`),
  INDEX `index_read_at` (`read_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS ezy_links (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `link_type` varchar(25) COLLATE utf8mb4_unicode_520_ci,
  `link_uri` varchar(500) NOT NULL,
  `image_id` bigint unsigned NOT NULL DEFAULT 0,
  `description` varchar(12000) COLLATE utf8mb4_unicode_520_ci,
  `source_type` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'UNKNOWN',
  `source_id` bigint unsigned NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_link_uri` (`link_uri`),
  INDEX `link_type` (`link_type`),
  INDEX `source_type_id` (`source_type`, `source_id`),
  INDEX `source_type` (`source_type`),
  INDEX `created_at` (`created_at`),
  INDEX `updated_at` (`updated_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_content_templates` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `template_type` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `template_name` varchar(300) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `title_template` varchar(1200) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `content_template` varchar(12000) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `creator_id` bigint unsigned NOT NULL,
  `status` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'DRAFT',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_template_type_name` (`template_type`, `template_name`),
  INDEX `index_template_type` (`template_type`),
  INDEX `index_template_name` (`template_name`),
  INDEX `index_creator_id` (`creator_id`),
  INDEX `index_status` (`status`),
  INDEX `index_created_at` (`created_at`),
  INDEX `index_updated_at` (`updated_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_admin_activity_histories` (
	`id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `admin_id` bigint unsigned NOT NULL,
    `feature` varchar(300) COLLATE utf8mb4_unicode_520_ci,
    `method` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `uri` varchar(300) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `uri_type` varchar(25) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `parameters` varchar(1200) COLLATE utf8mb4_unicode_520_ci,
    `created_at` datetime NOT NULL,
	PRIMARY KEY (`id`),
    INDEX `index_admin_id` (`admin_id`),
    INDEX `index_feature` (`feature`),
    INDEX `index_method` (`method`),
    INDEX `index_uri_type` (`uri_type`),
    INDEX `index_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_data_indices` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `data_type` varchar(120) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `data_id` bigint unsigned NOT NULL,
	`keyword` varchar(300) COLLATE utf8mb4_unicode_520_ci NOT NULL,
    `priority` int unsigned NOT NULL,
    `created_at` datetime NOT NULL,
    `updated_at` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_data_type_id_keyword` (`data_type`, `data_id`, `keyword`),
    INDEX `index_data_type` (`data_type`),
    INDEX `index_data_id` (`data_id`),
    INDEX `index_keyword` (`keyword`),
    INDEX `index_priority` (`priority`),
    INDEX `index_key_data_type_id_keyword_priority` (`data_type`, `data_id`, `keyword`, `priority`),
    INDEX `index_created_at` (`created_at`),
    INDEX `index_updated_at` (`updated_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE IF NOT EXISTS `ezy_data_meta` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `data_type` varchar(120) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
    `data_id` bigint unsigned NOT NULL DEFAULT 0,
    `meta_key` varchar(120) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
    `meta_value` varchar(300) COLLATE utf8mb4_unicode_520_ci,
    `meta_number_value` bigint NOT NULL default 0,
    PRIMARY KEY (`id`),
    INDEX `index_key_data_type` (`data_type`),
    INDEX `index_key_data_id` (`data_id`),
    INDEX `index_meta_key` (`meta_key`),
    INDEX `index_meta_value` (`meta_value`),
    INDEX `index_meta_number_value` (`meta_number_value`),
    INDEX `index_data_type_meta_key_value` (`data_type`, `meta_key`, `meta_value`),
    INDEX `index_data_type_meta_key_number_value` (`data_type`, `meta_key`, `meta_number_value`),
    INDEX `index_data_type_data_id_meta_key` (`data_type`, `data_id`, `meta_key`),
    INDEX `index_data_type_data_id_meta_key_value` (`data_type`, `data_id`, `meta_key`, `meta_value`),
    INDEX `index_data_type_data_id_meta_key_number_value` (`data_type`, `data_id`, `meta_key`, `meta_number_value`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
