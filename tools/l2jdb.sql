/*
Navicat MySQL Data Transfer

Source Server         : Mysql_Old
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : l2jdb

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2026-05-20 17:08:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `accounts`
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `login` varchar(45) NOT NULL DEFAULT '',
  `password` varchar(60) NOT NULL DEFAULT '',
  `last_active` bigint(20) NOT NULL DEFAULT 0,
  `access_level` int(3) NOT NULL DEFAULT 0,
  `last_server` int(4) NOT NULL DEFAULT 1,
  PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` VALUES ('admin', '$2a$10$tzNMfLL3HMegc7d1FQWIjeDvt60.oaPNTFgoFLzsca9M/afTSEBUm', '1779032479229', '0', '1');
INSERT INTO `accounts` VALUES ('astiler', '$2a$10$RljWkqDVHAfDdLwR2I0CTeY8ZlzEPBJdSjoaokwZnHKU0YAJD0Jqa', '1773010895892', '0', '1');
INSERT INTO `accounts` VALUES ('dhiusefe', '$2a$10$qHWRQ1pDiURyD1Uyqpsw6uQGQyiNReJEnC3aVW/XSKD5fh1l510Qq', '1770652399953', '0', '1');
INSERT INTO `accounts` VALUES ('dhousefe', '$2a$10$oJ/xtmqfICDdZGyoV1xzaOUTwHEr9KPgFZRQsd39Hb97tElbycI6y', '1779163683952', '0', '1');
INSERT INTO `accounts` VALUES ('jogar', '$2a$10$1.PStQ0gf0mKyQW9TE/kAuGIewgQYjlIrsjcqn8G7MW32zXN60boC', '1776125730884', '0', '1');
INSERT INTO `accounts` VALUES ('santiagoice', '$2a$10$TFju17EFucgg17mUYEAaP.lVP2vVkA7gr9V/h.2LvYwS1WNdwlYh2', '1773109715051', '0', '1');
INSERT INTO `accounts` VALUES ('socerer', '$2a$10$iie.YwPk0JmPpuJWuwHGq.CawczP2Ly0USGDIQuvv0eXCZgMalsP6', '1777863080154', '0', '1');
INSERT INTO `accounts` VALUES ('testando', '$2a$10$hKcz/3NYDvsIaFkzTEfAM.r.brISWlE/.J1ZQIC8UnH9BtUT02tvi', '1771881166095', '0', '1');
INSERT INTO `accounts` VALUES ('testano', '$2a$10$95krMavmVcIg.e4XGOgQluuxG1pDM141e/25IySSkUk07tv/OU4JS', '1773969693922', '0', '1');
INSERT INTO `accounts` VALUES ('teste', '$2a$10$nj4CYbHOVD0DYeI.CsdGBee3hyOBkMx.pJiOW6d0X0FXiK7lhaiJC', '1777079040807', '0', '1');
INSERT INTO `accounts` VALUES ('tester', '$2a$10$X1dG73Gk4/reyiW0r6UZYe7Wl3S9rH.ChfpZZBBRUfvRaPJDiGAUu', '1770936753068', '0', '1');
INSERT INTO `accounts` VALUES ('testnado', '$2a$10$NP6a.OTAsYjyDBf6bacKEOiYabyQhvPAG9v3sLk9X3.WYAGCQLKwG', '1777071417174', '0', '1');

-- ----------------------------
-- Table structure for `account_premium`
-- ----------------------------
DROP TABLE IF EXISTS `account_premium`;
CREATE TABLE `account_premium` (
  `account_name` varchar(45) NOT NULL DEFAULT '',
  `premium_service` int(1) NOT NULL DEFAULT 0,
  `enddate` decimal(20,0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`account_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of account_premium
-- ----------------------------
INSERT INTO `account_premium` VALUES ('admin', '0', '0');
INSERT INTO `account_premium` VALUES ('astiler', '0', '0');
INSERT INTO `account_premium` VALUES ('dhousefe', '0', '0');
INSERT INTO `account_premium` VALUES ('jogar', '1', '1776212234767');
INSERT INTO `account_premium` VALUES ('root', '0', '0');
INSERT INTO `account_premium` VALUES ('santiagoice', '0', '0');
INSERT INTO `account_premium` VALUES ('socerer', '1', '1777504360749');
INSERT INTO `account_premium` VALUES ('testando', '0', '0');
INSERT INTO `account_premium` VALUES ('testano', '1', '1774142611521');
INSERT INTO `account_premium` VALUES ('tester', '0', '0');
INSERT INTO `account_premium` VALUES ('testnado', '1', '1777244303276');

-- ----------------------------
-- Table structure for `auctions`
-- ----------------------------
DROP TABLE IF EXISTS `auctions`;
CREATE TABLE `auctions` (
  `clanhall_id` int(10) unsigned NOT NULL DEFAULT 0,
  `bidder_name` varchar(35) NOT NULL DEFAULT '',
  `clan_oid` int(10) unsigned NOT NULL DEFAULT 0,
  `clan_name` varchar(20) NOT NULL DEFAULT '',
  `max_bid` int(10) unsigned NOT NULL DEFAULT 0,
  `time_bid` bigint(20) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`clanhall_id`,`clan_oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of auctions
-- ----------------------------

-- ----------------------------
-- Table structure for `augmentations`
-- ----------------------------
DROP TABLE IF EXISTS `augmentations`;
CREATE TABLE `augmentations` (
  `item_oid` int(10) unsigned NOT NULL DEFAULT 0,
  `attributes` int(11) NOT NULL DEFAULT -1,
  `skill_id` int(11) NOT NULL DEFAULT -1,
  `skill_level` int(11) NOT NULL DEFAULT -1,
  PRIMARY KEY (`item_oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of augmentations
-- ----------------------------

-- ----------------------------
-- Table structure for `autofarm_areas`
-- ----------------------------
DROP TABLE IF EXISTS `autofarm_areas`;
CREATE TABLE `autofarm_areas` (
  `player_id` int(10) unsigned NOT NULL DEFAULT 0,
  `area_id` int(10) unsigned NOT NULL DEFAULT 0,
  `name` varchar(44) DEFAULT NULL,
  `type` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`player_id`,`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of autofarm_areas
-- ----------------------------
INSERT INTO `autofarm_areas` VALUES ('268497182', '268494488', 'testezoneket', 'ZONA');
INSERT INTO `autofarm_areas` VALUES ('268497182', '268494941', 'testerota', 'ROTA');
INSERT INTO `autofarm_areas` VALUES ('268497394', '268493720', 'testando', 'ROTA');
INSERT INTO `autofarm_areas` VALUES ('268509776', '268513973', 'cata', 'ROTA');

-- ----------------------------
-- Table structure for `autofarm_nodes`
-- ----------------------------
DROP TABLE IF EXISTS `autofarm_nodes`;
CREATE TABLE `autofarm_nodes` (
  `node_id` int(10) unsigned NOT NULL DEFAULT 0,
  `area_id` int(11) NOT NULL DEFAULT 0,
  `loc_x` int(11) NOT NULL DEFAULT 0,
  `loc_y` int(11) NOT NULL DEFAULT 0,
  `loc_z` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`area_id`,`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of autofarm_nodes
-- ----------------------------
INSERT INTO `autofarm_nodes` VALUES ('0', '268493720', '170820', '21690', '-3395');
INSERT INTO `autofarm_nodes` VALUES ('1', '268493720', '171119', '21480', '-3329');
INSERT INTO `autofarm_nodes` VALUES ('2', '268493720', '171821', '22339', '-3329');
INSERT INTO `autofarm_nodes` VALUES ('3', '268493720', '172018', '23290', '-3329');
INSERT INTO `autofarm_nodes` VALUES ('0', '268494488', '144608', '-71175', '-4138');
INSERT INTO `autofarm_nodes` VALUES ('1', '268494488', '144107', '-71352', '-4117');
INSERT INTO `autofarm_nodes` VALUES ('2', '268494488', '144432', '-72606', '-4234');
INSERT INTO `autofarm_nodes` VALUES ('3', '268494488', '145292', '-72780', '-4298');
INSERT INTO `autofarm_nodes` VALUES ('4', '268494488', '144821', '-71638', '-4183');
INSERT INTO `autofarm_nodes` VALUES ('5', '268494488', '144635', '-71252', '-4140');
INSERT INTO `autofarm_nodes` VALUES ('0', '268494941', '118926', '-76401', '-3400');
INSERT INTO `autofarm_nodes` VALUES ('1', '268494941', '117910', '-76538', '-3400');
INSERT INTO `autofarm_nodes` VALUES ('2', '268494941', '117845', '-76924', '-3400');
INSERT INTO `autofarm_nodes` VALUES ('3', '268494941', '118181', '-77383', '-3400');
INSERT INTO `autofarm_nodes` VALUES ('4', '268494941', '117785', '-77804', '-3400');
INSERT INTO `autofarm_nodes` VALUES ('5', '268494941', '116381', '-77805', '-3400');
INSERT INTO `autofarm_nodes` VALUES ('0', '268513973', '-43002', '210419', '-5093');
INSERT INTO `autofarm_nodes` VALUES ('1', '268513973', '-43006', '211165', '-5093');
INSERT INTO `autofarm_nodes` VALUES ('2', '268513973', '-43005', '212272', '-5095');
INSERT INTO `autofarm_nodes` VALUES ('3', '268513973', '-42019', '212435', '-5095');
INSERT INTO `autofarm_nodes` VALUES ('4', '268513973', '-41971', '213441', '-5093');

-- ----------------------------
-- Table structure for `autofarm_player_data`
-- ----------------------------
DROP TABLE IF EXISTS `autofarm_player_data`;
CREATE TABLE `autofarm_player_data` (
  `player_id` int(11) NOT NULL,
  `time_used` bigint(20) DEFAULT 0,
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- ----------------------------
-- Records of autofarm_player_data
-- ----------------------------
INSERT INTO `autofarm_player_data` VALUES ('268493285', '128330');
INSERT INTO `autofarm_player_data` VALUES ('268493302', '1480770');
INSERT INTO `autofarm_player_data` VALUES ('268493309', '68161528');
INSERT INTO `autofarm_player_data` VALUES ('268493467', '1240403');
INSERT INTO `autofarm_player_data` VALUES ('268493550', '6831384');
INSERT INTO `autofarm_player_data` VALUES ('268493578', '1063053');
INSERT INTO `autofarm_player_data` VALUES ('268493605', '2399594');
INSERT INTO `autofarm_player_data` VALUES ('268493707', '5479674');
INSERT INTO `autofarm_player_data` VALUES ('268493809', '194192');
INSERT INTO `autofarm_player_data` VALUES ('268493876', '28314210');
INSERT INTO `autofarm_player_data` VALUES ('268493896', '6345534');
INSERT INTO `autofarm_player_data` VALUES ('268493913', '1087405');
INSERT INTO `autofarm_player_data` VALUES ('268494228', '5943989');
INSERT INTO `autofarm_player_data` VALUES ('268494324', '1780413');
INSERT INTO `autofarm_player_data` VALUES ('268494356', '24121356');
INSERT INTO `autofarm_player_data` VALUES ('268494406', '1492162');
INSERT INTO `autofarm_player_data` VALUES ('268494667', '6899852');
INSERT INTO `autofarm_player_data` VALUES ('268494751', '104612205');
INSERT INTO `autofarm_player_data` VALUES ('268495502', '131763491');
INSERT INTO `autofarm_player_data` VALUES ('268495632', '915254');
INSERT INTO `autofarm_player_data` VALUES ('268495874', '165340');
INSERT INTO `autofarm_player_data` VALUES ('268497182', '110627731');
INSERT INTO `autofarm_player_data` VALUES ('268497394', '39590582');
INSERT INTO `autofarm_player_data` VALUES ('268498074', '31744');
INSERT INTO `autofarm_player_data` VALUES ('268498107', '838667');
INSERT INTO `autofarm_player_data` VALUES ('268498118', '369067');
INSERT INTO `autofarm_player_data` VALUES ('268498467', '31666583');
INSERT INTO `autofarm_player_data` VALUES ('268503745', '276644');
INSERT INTO `autofarm_player_data` VALUES ('268504379', '2921388');
INSERT INTO `autofarm_player_data` VALUES ('268509776', '5091081');
INSERT INTO `autofarm_player_data` VALUES ('268523291', '12594754');
INSERT INTO `autofarm_player_data` VALUES ('268543578', '7419758');
INSERT INTO `autofarm_player_data` VALUES ('268631868', '51915634');

-- ----------------------------
-- Table structure for `autofarm_skills`
-- ----------------------------
DROP TABLE IF EXISTS `autofarm_skills`;
CREATE TABLE `autofarm_skills` (
  `player_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `slot` int(11) NOT NULL,
  PRIMARY KEY (`player_id`,`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- ----------------------------
-- Records of autofarm_skills
-- ----------------------------
INSERT INTO `autofarm_skills` VALUES ('268493285', '3', '0');
INSERT INTO `autofarm_skills` VALUES ('268493309', '1015', '1');
INSERT INTO `autofarm_skills` VALUES ('268493309', '1071', '4');
INSERT INTO `autofarm_skills` VALUES ('268493309', '1235', '2');
INSERT INTO `autofarm_skills` VALUES ('268493309', '1236', '3');
INSERT INTO `autofarm_skills` VALUES ('268493309', '1264', '0');
INSERT INTO `autofarm_skills` VALUES ('268493309', '1265', '5');
INSERT INTO `autofarm_skills` VALUES ('268493550', '1071', '4');
INSERT INTO `autofarm_skills` VALUES ('268493550', '1235', '0');
INSERT INTO `autofarm_skills` VALUES ('268493550', '1265', '3');
INSERT INTO `autofarm_skills` VALUES ('268493550', '1340', '1');
INSERT INTO `autofarm_skills` VALUES ('268493550', '1342', '2');
INSERT INTO `autofarm_skills` VALUES ('268493578', '1', '2');
INSERT INTO `autofarm_skills` VALUES ('268493578', '78', '4');
INSERT INTO `autofarm_skills` VALUES ('268493605', '3', '0');
INSERT INTO `autofarm_skills` VALUES ('268493605', '19', '1');
INSERT INTO `autofarm_skills` VALUES ('268493605', '56', '2');
INSERT INTO `autofarm_skills` VALUES ('268493605', '99', '4');
INSERT INTO `autofarm_skills` VALUES ('268493605', '101', '3');
INSERT INTO `autofarm_skills` VALUES ('268493707', '3', '0');
INSERT INTO `autofarm_skills` VALUES ('268493809', '1177', '0');
INSERT INTO `autofarm_skills` VALUES ('268493876', '16', '0');
INSERT INTO `autofarm_skills` VALUES ('268493896', '36', '1');
INSERT INTO `autofarm_skills` VALUES ('268493896', '245', '2');
INSERT INTO `autofarm_skills` VALUES ('268493896', '302', '0');
INSERT INTO `autofarm_skills` VALUES ('268493913', '3', '0');
INSERT INTO `autofarm_skills` VALUES ('268493913', '16', '2');
INSERT INTO `autofarm_skills` VALUES ('268493913', '263', '1');
INSERT INTO `autofarm_skills` VALUES ('268494228', '1071', '0');
INSERT INTO `autofarm_skills` VALUES ('268494228', '1235', '2');
INSERT INTO `autofarm_skills` VALUES ('268494228', '1236', '5');
INSERT INTO `autofarm_skills` VALUES ('268494228', '1265', '1');
INSERT INTO `autofarm_skills` VALUES ('268494228', '1340', '3');
INSERT INTO `autofarm_skills` VALUES ('268494228', '1342', '4');
INSERT INTO `autofarm_skills` VALUES ('268494324', '3', '0');
INSERT INTO `autofarm_skills` VALUES ('268494324', '36', '3');
INSERT INTO `autofarm_skills` VALUES ('268494324', '48', '4');
INSERT INTO `autofarm_skills` VALUES ('268494324', '78', '1');
INSERT INTO `autofarm_skills` VALUES ('268494324', '245', '5');
INSERT INTO `autofarm_skills` VALUES ('268494324', '286', '2');
INSERT INTO `autofarm_skills` VALUES ('268494356', '3', '1');
INSERT INTO `autofarm_skills` VALUES ('268494406', '1083', '2');
INSERT INTO `autofarm_skills` VALUES ('268494406', '1230', '1');
INSERT INTO `autofarm_skills` VALUES ('268494406', '1231', '3');
INSERT INTO `autofarm_skills` VALUES ('268494406', '1339', '0');
INSERT INTO `autofarm_skills` VALUES ('268494667', '4', '1');
INSERT INTO `autofarm_skills` VALUES ('268494667', '19', '2');
INSERT INTO `autofarm_skills` VALUES ('268494667', '99', '3');
INSERT INTO `autofarm_skills` VALUES ('268494667', '101', '4');
INSERT INTO `autofarm_skills` VALUES ('268494667', '131', '0');
INSERT INTO `autofarm_skills` VALUES ('268494751', '1015', '0');
INSERT INTO `autofarm_skills` VALUES ('268494751', '1074', '4');
INSERT INTO `autofarm_skills` VALUES ('268494751', '1230', '3');
INSERT INTO `autofarm_skills` VALUES ('268494751', '1234', '5');
INSERT INTO `autofarm_skills` VALUES ('268494751', '1239', '1');
INSERT INTO `autofarm_skills` VALUES ('268494751', '1296', '2');
INSERT INTO `autofarm_skills` VALUES ('268495502', '3', '0');
INSERT INTO `autofarm_skills` VALUES ('268495632', '16', '0');
INSERT INTO `autofarm_skills` VALUES ('268497182', '7', '6');
INSERT INTO `autofarm_skills` VALUES ('268497182', '78', '5');
INSERT INTO `autofarm_skills` VALUES ('268497182', '261', '4');
INSERT INTO `autofarm_skills` VALUES ('268497182', '343', '2');
INSERT INTO `autofarm_skills` VALUES ('268497182', '345', '1');
INSERT INTO `autofarm_skills` VALUES ('268497182', '423', '3');
INSERT INTO `autofarm_skills` VALUES ('268497394', '94', '1');
INSERT INTO `autofarm_skills` VALUES ('268497394', '100', '0');
INSERT INTO `autofarm_skills` VALUES ('268497394', '190', '4');
INSERT INTO `autofarm_skills` VALUES ('268497394', '255', '2');
INSERT INTO `autofarm_skills` VALUES ('268498074', '56', '0');
INSERT INTO `autofarm_skills` VALUES ('268498107', '1090', '0');
INSERT INTO `autofarm_skills` VALUES ('268498118', '3', '0');
INSERT INTO `autofarm_skills` VALUES ('268498467', '100', '0');
INSERT INTO `autofarm_skills` VALUES ('268498467', '302', '1');
INSERT INTO `autofarm_skills` VALUES ('268503745', '1147', '2');
INSERT INTO `autofarm_skills` VALUES ('268503745', '1168', '1');
INSERT INTO `autofarm_skills` VALUES ('268503745', '1177', '0');
INSERT INTO `autofarm_skills` VALUES ('268504379', '1071', '3');
INSERT INTO `autofarm_skills` VALUES ('268504379', '1231', '0');
INSERT INTO `autofarm_skills` VALUES ('268504379', '1235', '1');
INSERT INTO `autofarm_skills` VALUES ('268504379', '1265', '2');
INSERT INTO `autofarm_skills` VALUES ('268509776', '100', '2');
INSERT INTO `autofarm_skills` VALUES ('268509776', '254', '0');
INSERT INTO `autofarm_skills` VALUES ('268509776', '260', '1');
INSERT INTO `autofarm_skills` VALUES ('268509776', '302', '3');
INSERT INTO `autofarm_skills` VALUES ('268523291', '254', '0');
INSERT INTO `autofarm_skills` VALUES ('268543578', '1015', '2');
INSERT INTO `autofarm_skills` VALUES ('268543578', '1147', '5');
INSERT INTO `autofarm_skills` VALUES ('268543578', '1172', '1');
INSERT INTO `autofarm_skills` VALUES ('268543578', '1177', '0');
INSERT INTO `autofarm_skills` VALUES ('268543578', '1178', '3');
INSERT INTO `autofarm_skills` VALUES ('268543578', '1181', '4');
INSERT INTO `autofarm_skills` VALUES ('268631868', '1', '2');
INSERT INTO `autofarm_skills` VALUES ('268631868', '6', '3');
INSERT INTO `autofarm_skills` VALUES ('268631868', '9', '1');
INSERT INTO `autofarm_skills` VALUES ('268631868', '345', '0');

-- ----------------------------
-- Table structure for `autofarm_time_usage`
-- ----------------------------
DROP TABLE IF EXISTS `autofarm_time_usage`;
CREATE TABLE `autofarm_time_usage` (
  `player_id` int(11) NOT NULL,
  `time_used` bigint(20) DEFAULT 0,
  `last_reset` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of autofarm_time_usage
-- ----------------------------
INSERT INTO `autofarm_time_usage` VALUES ('268492127', '-650000', '2025-09-13 00:58:49');
INSERT INTO `autofarm_time_usage` VALUES ('268493191', '693599978', '2025-12-19 19:50:03');
INSERT INTO `autofarm_time_usage` VALUES ('268493550', '8567683524', '2025-12-19 00:05:04');

-- ----------------------------
-- Table structure for `balance_classes`
-- ----------------------------
DROP TABLE IF EXISTS `balance_classes`;
CREATE TABLE `balance_classes` (
  `class_id_attacker` int(11) NOT NULL,
  `class_id_target` int(11) NOT NULL,
  `p_atk_mod` decimal(5,2) DEFAULT 1.00,
  `m_atk_mod` decimal(5,2) DEFAULT 1.00,
  `p_def_mod` decimal(5,2) DEFAULT 1.00,
  `m_def_mod` decimal(5,2) DEFAULT 1.00,
  PRIMARY KEY (`class_id_attacker`,`class_id_target`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of balance_classes
-- ----------------------------
INSERT INTO `balance_classes` VALUES ('88', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('88', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('88', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('89', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('89', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('90', '88', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '89', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '90', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '91', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '92', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '93', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '94', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '95', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '96', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '97', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '98', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '99', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '100', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '101', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '102', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '103', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '104', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '105', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '106', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '107', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '108', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '109', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '110', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('90', '111', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '112', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '113', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '114', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '115', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '116', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '117', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('90', '118', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('91', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('91', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('91', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('92', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('92', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('93', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('93', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('94', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('94', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('95', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('95', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('96', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('96', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('97', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('97', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('98', '88', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '89', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '90', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '91', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '92', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '93', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '94', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '95', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '96', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '97', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '98', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '99', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '100', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '101', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '102', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '103', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '104', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '105', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '106', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '107', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '108', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '109', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '110', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('98', '111', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '112', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '113', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '114', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '115', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '116', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '117', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('98', '118', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('99', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('99', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('99', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('100', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('100', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('101', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('101', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('102', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('102', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('103', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('103', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('104', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('104', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('105', '88', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '89', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '90', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '91', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '92', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '93', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '94', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '95', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '96', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '97', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '98', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '99', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '100', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '101', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '102', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '103', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '104', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '105', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '106', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '107', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '108', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '109', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '110', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('105', '111', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '112', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '113', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '114', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '115', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '116', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '117', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('105', '118', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('106', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('106', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('106', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('107', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('107', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('108', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('108', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('109', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('109', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '88', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '89', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '90', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '91', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '92', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '93', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '94', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '95', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '96', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '97', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '98', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '99', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '100', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '101', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '102', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '103', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '104', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '105', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '106', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '107', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '108', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '109', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '110', '0.80', '0.80', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('110', '111', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '112', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '113', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '114', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '115', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '116', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '117', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('110', '118', '0.80', '0.80', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('111', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('111', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('112', '88', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '89', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '90', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '91', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '92', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '93', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '94', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '95', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '96', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '97', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '98', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '99', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '100', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '101', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '102', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '103', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '104', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '105', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '106', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '107', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '108', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '109', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '110', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('112', '111', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '112', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '113', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '114', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '115', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '116', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '117', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('112', '118', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('113', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('113', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('113', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('114', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('114', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('115', '88', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '89', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '90', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '91', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '92', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '93', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '94', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '95', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '96', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '97', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '98', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '99', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '100', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '101', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '102', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '103', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '104', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '105', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '106', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '107', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '108', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '109', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '110', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('115', '111', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '112', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '113', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '114', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '115', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '116', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '117', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('115', '118', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '88', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '89', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '90', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '91', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '92', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '93', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '94', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '95', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '96', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '97', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '98', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '99', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '100', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '101', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '102', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '103', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '104', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '105', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '106', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '107', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '108', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '109', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '110', '1.30', '1.30', '0.90', '0.90');
INSERT INTO `balance_classes` VALUES ('116', '111', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '112', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '113', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '114', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '115', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '116', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '117', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('116', '118', '1.30', '1.30', '1.10', '1.10');
INSERT INTO `balance_classes` VALUES ('117', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('117', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('117', '118', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '88', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '89', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '90', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '91', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '92', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '93', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '94', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '95', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '96', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '97', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '98', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '99', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '100', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '101', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '102', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '103', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '104', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '105', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '106', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '107', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '108', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '109', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '110', '1.00', '1.00', '0.80', '0.80');
INSERT INTO `balance_classes` VALUES ('118', '111', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '112', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '113', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '114', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '115', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '116', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '117', '1.00', '1.00', '1.00', '1.00');
INSERT INTO `balance_classes` VALUES ('118', '118', '1.00', '1.00', '1.00', '1.00');

-- ----------------------------
-- Table structure for `balance_vulnerability`
-- ----------------------------
DROP TABLE IF EXISTS `balance_vulnerability`;
CREATE TABLE `balance_vulnerability` (
  `skill_type` varchar(50) NOT NULL,
  `multiplier` decimal(5,2) NOT NULL DEFAULT 1.00,
  PRIMARY KEY (`skill_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of balance_vulnerability
-- ----------------------------
INSERT INTO `balance_vulnerability` VALUES ('BLEED', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('CANCEL', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('CONFUSION', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('DEBUFF', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('DERANGEMENT', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('PARALYZE', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('POISON', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('ROOT', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('SLEEP', '1.00');
INSERT INTO `balance_vulnerability` VALUES ('STUN', '1.00');

-- ----------------------------
-- Table structure for `bbs_auction`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_auction`;
CREATE TABLE `bbs_auction` (
  `id` int(10) NOT NULL DEFAULT 0,
  `obj_Id` int(10) NOT NULL DEFAULT 0,
  `item_id` int(10) NOT NULL DEFAULT 0,
  `item_count` int(10) NOT NULL DEFAULT 0,
  `item_enchant` int(10) NOT NULL DEFAULT 0,
  `price_id` int(10) NOT NULL DEFAULT 0,
  `price_count` int(10) NOT NULL DEFAULT 0,
  `duration` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bbs_auction
-- ----------------------------

-- ----------------------------
-- Table structure for `bbs_favorite`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_favorite`;
CREATE TABLE `bbs_favorite` (
  `id` int(10) unsigned NOT NULL DEFAULT 0,
  `player_id` int(10) unsigned NOT NULL DEFAULT 0,
  `title` varchar(35) DEFAULT NULL,
  `bypass` varchar(128) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bbs_favorite
-- ----------------------------

-- ----------------------------
-- Table structure for `bbs_forum`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_forum`;
CREATE TABLE `bbs_forum` (
  `id` int(8) NOT NULL DEFAULT 0,
  `type` varchar(10) NOT NULL DEFAULT '0',
  `access` varchar(12) NOT NULL DEFAULT '0',
  `owner_id` int(8) NOT NULL DEFAULT 0,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bbs_forum
-- ----------------------------

-- ----------------------------
-- Table structure for `bbs_mail`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_mail`;
CREATE TABLE `bbs_mail` (
  `id` int(10) unsigned NOT NULL DEFAULT 0,
  `receiver_id` int(10) unsigned NOT NULL DEFAULT 0,
  `sender_id` int(10) unsigned NOT NULL DEFAULT 0,
  `location` varchar(15) NOT NULL,
  `recipients` varchar(200) DEFAULT NULL,
  `subject` varchar(128) DEFAULT NULL,
  `message` varchar(3000) DEFAULT NULL,
  `sent_date` timestamp NULL DEFAULT NULL,
  `is_unread` smallint(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bbs_mail
-- ----------------------------

-- ----------------------------
-- Table structure for `bbs_post`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_post`;
CREATE TABLE `bbs_post` (
  `id` int(8) NOT NULL DEFAULT 0,
  `owner_name` varchar(255) NOT NULL DEFAULT '',
  `owner_id` int(8) NOT NULL DEFAULT 0,
  `date` decimal(20,0) NOT NULL DEFAULT 0,
  `topic_id` int(8) NOT NULL DEFAULT 0,
  `forum_id` int(8) NOT NULL DEFAULT 0,
  `txt` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bbs_post
-- ----------------------------

-- ----------------------------
-- Table structure for `bbs_topic`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_topic`;
CREATE TABLE `bbs_topic` (
  `id` int(8) NOT NULL DEFAULT 0,
  `forum_id` int(8) NOT NULL DEFAULT 0,
  `name` varchar(255) NOT NULL DEFAULT '',
  `date` decimal(20,0) NOT NULL DEFAULT 0,
  `owner_name` varchar(255) NOT NULL DEFAULT '0',
  `owner_id` int(8) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bbs_topic
-- ----------------------------

-- ----------------------------
-- Table structure for `bookmarks`
-- ----------------------------
DROP TABLE IF EXISTS `bookmarks`;
CREATE TABLE `bookmarks` (
  `name` varchar(45) NOT NULL DEFAULT '',
  `obj_Id` int(10) unsigned NOT NULL DEFAULT 0,
  `x` mediumint(9) DEFAULT NULL,
  `y` mediumint(9) DEFAULT NULL,
  `z` mediumint(9) DEFAULT NULL,
  PRIMARY KEY (`name`,`obj_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bookmarks
-- ----------------------------

-- ----------------------------
-- Table structure for `buffer_schemes`
-- ----------------------------
DROP TABLE IF EXISTS `buffer_schemes`;
CREATE TABLE `buffer_schemes` (
  `object_id` int(10) unsigned NOT NULL DEFAULT 0,
  `scheme_name` varchar(16) NOT NULL DEFAULT 'default',
  `skills` varchar(200) NOT NULL,
  `levels` varchar(200) NOT NULL,
  PRIMARY KEY (`object_id`,`scheme_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of buffer_schemes
-- ----------------------------
INSERT INTO `buffer_schemes` VALUES ('268493309', 'Mago', '1035,1036,1040,1045,1059,1062,1078,1085,1204,1243,1044,1304,1303,1389,1413,273,276,365,264,267,268,349,304', '4,2,3,6,3,2,6,3,2,6,3,3,2,3,1,1,1,1,1,1,1,1,1');
INSERT INTO `buffer_schemes` VALUES ('268493896', 'Fighter', '1036,1040,1045,1062,1068,1086,1077,1204,1243,1242,1304,1268,1388,1363,4700,4554,271,274,275,310,264,268,269,364', '2,3,6,2,3,2,3,2,6,3,3,4,3,1,13,4,1,1,1,1,1,1,1,1');
INSERT INTO `buffer_schemes` VALUES ('268493913', 'Fighterdagger', '1036,1040,1045,1062,1068,1077,1086,1087,1204,1240,1242,1268,1363,1388,4700,271,274,275,310,264,266,267,268,269,304', '2,3,6,2,3,3,2,3,2,3,3,4,1,3,13,1,1,1,1,1,1,1,1,1,1');
INSERT INTO `buffer_schemes` VALUES ('268494048', 'Titan', '1036,1040,1045,1068,1077,1086,1242,1204,1268,1363,1388,271,272,274,275,310,264,269,304,364,267,268', '2,3,6,3,3,2,3,2,4,1,3,1,1,1,1,1,1,1,1,1,1,1');
INSERT INTO `buffer_schemes` VALUES ('268494228', 'Magos', '1036,1040,1045,1059,273,276,365,1062,1078,1085,1204,1243,1303,1304,1389,1413,264,267,268,304,349,1035', '2,3,6,3,1,1,1,2,6,3,2,6,2,3,3,1,1,1,1,1,1,4');
INSERT INTO `buffer_schemes` VALUES ('268494324', 'Poller', '1036,1040,1045,1062,1068,1077,1086,1204,1242,1268,1363,1388,1259,1284,4700,4554,271,274,275,310,264,267,268,269,304', '2,3,6,2,3,3,2,2,3,4,1,3,4,3,13,4,1,1,1,1,1,1,1,1,1');
INSERT INTO `buffer_schemes` VALUES ('268494751', 'mager', '1035,1036,1040,1045,1059,1062,1078,1085,1204,1243,1303,1304,1389,1413,273,276,365,264,267,268,304,349,1259,305,4703,4554,363', '4,2,3,6,3,2,6,3,2,6,2,3,3,1,1,1,1,1,1,1,1,1,4,1,13,4,1');
INSERT INTO `buffer_schemes` VALUES ('268497182', 'dfbdfb', '', '');
INSERT INTO `buffer_schemes` VALUES ('268497394', 'fighter', '1036,1040,1045,1068,1077,1086,1204,1240,1242,1268,1363,1388,271,272,274,275,310,264,269,268,364,304', '2,3,6,3,3,2,2,3,3,4,1,3,1,1,1,1,1,1,1,1,1,1');
INSERT INTO `buffer_schemes` VALUES ('268509776', 'FIghter', '1036,1040,1045,1062,1068,1077,1086,1204,1240,1242,1243,1304,1268,1363,1388,1259,271,272,274,275,310,264,268,269,304', '2,3,6,2,3,3,2,2,3,3,6,3,4,1,3,4,1,1,1,1,1,1,1,1,1');

-- ----------------------------
-- Table structure for `buffshop`
-- ----------------------------
DROP TABLE IF EXISTS `buffshop`;
CREATE TABLE `buffshop` (
  `ownerId` int(11) NOT NULL,
  `buffs` text NOT NULL,
  `title` varchar(255) NOT NULL DEFAULT '',
  `x` int(7) DEFAULT NULL,
  `y` int(7) DEFAULT NULL,
  `z` int(7) DEFAULT NULL,
  `heading` int(7) DEFAULT NULL,
  `tempBuffShopPrice` varchar(32) DEFAULT NULL,
  `store_message` varchar(50) DEFAULT NULL,
  `value` varchar(64) DEFAULT NULL,
  `class_id` int(11) NOT NULL DEFAULT 0,
  `sex` tinyint(1) NOT NULL DEFAULT 0,
  `face` tinyint(1) NOT NULL DEFAULT 0,
  `hair_style` tinyint(1) NOT NULL DEFAULT 0,
  `hair_color` tinyint(1) NOT NULL DEFAULT 0,
  `equipped_items` text DEFAULT NULL,
  PRIMARY KEY (`ownerId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- ----------------------------
-- Records of buffshop
-- ----------------------------
INSERT INTO `buffshop` VALUES ('268494542', '264,1,10;265,1,10;266,1,10;267,1,10;268,1,10;269,1,10;270,1,10;304,1,10;305,1,10;306,1,10;308,1,10;349,1,10;363,1,10;364,1,10', 'Buff Song!', '-12397', '122638', '-3096', '36737', '', 'Buff Sword Muse!', '', '100', '0', '0', '0', '0', '850,850,913,881,881,2411,159,2493,2449,352,2378,2425');
INSERT INTO `buffshop` VALUES ('268494262', '1303,2,0;1259,4,2000;1355,1,25060;1304,3,256;1354,1,25;1243,6,25;1397,3,25;1078,6,25;1257,3,25;1353,1,25;1043,1,25;1073,2,25;1035,4,25;1068,3,25;1044,3,25;1033,3,25;1040,3,25;1393,3,25;1087,3,25', 'master', '-12508', '122914', '-3104', '34248', null, 'Buffs Eva\'s Saint Lv78', null, '105', '0', '0', '0', '0', '850,850,913,881,881,547,6608,6377,5770,2400,2405,5782');
INSERT INTO `buffshop` VALUES ('268501423', '1003,3,26;1004,3,26;1005,3,26;1008,3,26;1249,3,26;1250,3,26;1260,3,26;1261,2,26;1282,2,26;1323,1,26;1325,1,26;1364,1,26;1365,1,26;1414,1,26;1415,1,26;1416,1,26;1427,1,26', 'Overlord', '-12394', '122751', '-3096', '30835', null, 'Buff Dominator Lv80!', null, '115', '0', '0', '0', '0', '850,850,913,881,881,2412,189,629,2450,437,470,2426');
INSERT INTO `buffshop` VALUES ('268495600', '33,8,23;1035,1,23;1040,1,23;1059,1,23;1068,1,23;1073,1,23;1077,1,23;1078,2,23;1139,2,23;1140,3,23;1141,2,23;1146,3,23;1189,1,23;1228,18,23;1268,1,23;1278,14,23;1281,9,23;1299,2,23;1330,8,23;1333,10,23;1348,1,23;1349,1,23', 'Summon', '-12384', '122849', '-3096', '37604', null, 'Buff Spectral Master Lv80!', null, '111', '0', '0', '0', '0', '850,850,913,881,881,2412,189,629,2450,437,470,2426');
INSERT INTO `buffshop` VALUES ('268493442', '1035,4,10;1036,2,10;1040,3,10;1045,6,10;1048,6,10;1062,2,10;1068,3,10;1085,3,10;1086,2,10;1078,6,10;1077,3,10;1240,3,10;1242,3,10;1243,3,10;1388,3,10;1389,3,10;1307,3,10', 'Hierophant', '-12501', '122589', '-3104', '43019', '', 'Buff Hierophant!', '', '98', '0', '1', '1', '1', '850,850,913,881,881,2412,189,629,2450,437,470,2426,7837');
INSERT INTO `buffshop` VALUES ('268515809', '1002,3,25;1003,1,25;1005,1,25;1006,3,25;1007,3,25;1009,3,25;1284,3,25;1253,3,25;1252,3,25;1251,2,25;1010,3,25;1308,3,25;1309,3,25;1310,4,25;1362,1,25;1363,1,25;1390,3,25;1391,3,25;1413,1,25', 'Doom Cryer', '-12505', '122738', '-3104', '26635', null, 'Buff Doom Cryer Lv80!', null, '116', '0', '0', '0', '0', '850,850,913,881,881,2412,189,629,2450,437,470,2426');
INSERT INTO `buffshop` VALUES ('268493246', '10,8,42;1139,2,42;1140,3,42;1141,2,42;1144,2,42;1225,18,42;1276,14,42;1279,9,42;1331,10,42;1299,2,42;1328,8,42;1346,1,42;1349,1,42;4699,3,42;4700,3,42', 'Summoner', '-12505', '122833', '-3104', '27658', null, 'Buff Arcana Lord Lv80!', null, '96', '1', '0', '0', '0', '850,850,913,881,881,2412,189,629,2450,437,470,2426');
INSERT INTO `buffshop` VALUES ('268494214', '271,1,10;272,1,10;273,1,10;274,1,10;275,1,10;276,1,10;309,1,10;310,1,10;311,1,10;365,1,10;307,1,10;277,1,10;366,1,10', 'Spectral Dancer', '-12407', '122546', '-3104', '32117', '', 'Buff Spectral Dancer!', '', '107', '1', '0', '0', '0', '850,850,913,881,881,2411,2499,2493,2449,352,2378,2425');
INSERT INTO `buffshop` VALUES ('268494929', '1035,4,1;1040,3,1;1059,3,1;1068,3,1;1073,2,1;1077,3,1;1078,6,1;1189,3,1;1240,3,1;1242,3,1;1268,4,1;1303,2,1;1354,1,1;1357,1,1;1392,3,1', 'Shillien Saint', '-12383', '122941', '-3104', '43326', null, 'Buff Shillien Saint Lv79!', null, '112', '1', '0', '0', '0', '850,850,913,881,881,2412,189,629,2450,437,470,2426');
INSERT INTO `buffshop` VALUES ('268493837', '67,7,45;1139,2,45;1140,3,45;1145,3,45;1226,18,45;1227,18,45;1277,14,45;1280,9,45;1329,9,45;1299,2,45;1347,1,45;1349,1,45;4702,3,45;4703,3,45', 'Summoner', '-12514', '122519', '-3104', '23630', null, 'Buff Elemental Master Lv80!', null, '104', '1', '2', '4', '1', '850,850,913,881,881,2412,189,629,2450,437,470,2426');
INSERT INTO `buffshop` VALUES ('268497182', '1062,1,0;1045,6,0;1284,3,0;1310,1,0;1363,1,0;274,1,0;275,1,0;272,1,0;310,1,0;271,1,0;1077,1,0;1388,3,0;1240,3,0;1086,2,0;1036,2,0;1323,1,0;1068,2,0;1259,1,0;1040,2,0;264,1,0;269,1,0;305,1,0;304,1,0;267,1,0;268,1,0;67,7,0;1364,1,0', 'Fighter', '-13009', '122543', '-3117', '59772', null, 'Buffs Fighter Lv85', null, '15', '0', '1', '1', '2', '15007,15001,15003,15009,15005,6382,5706,6380,6379,6381,7694');

-- ----------------------------
-- Table structure for `buylists`
-- ----------------------------
DROP TABLE IF EXISTS `buylists`;
CREATE TABLE `buylists` (
  `buylist_id` int(10) unsigned NOT NULL,
  `item_id` int(10) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL DEFAULT 0,
  `next_restock_time` bigint(20) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`buylist_id`,`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of buylists
-- ----------------------------

-- ----------------------------
-- Table structure for `castle`
-- ----------------------------
DROP TABLE IF EXISTS `castle`;
CREATE TABLE `castle` (
  `id` int(11) NOT NULL DEFAULT 0,
  `currentTaxPercent` int(11) NOT NULL DEFAULT 0,
  `nextTaxPercent` int(11) NOT NULL DEFAULT 0,
  `treasury` bigint(20) NOT NULL DEFAULT 0,
  `taxRevenue` bigint(20) NOT NULL DEFAULT 0,
  `seedIncome` bigint(20) NOT NULL DEFAULT 0,
  `siegeDate` decimal(20,0) NOT NULL DEFAULT 0,
  `regTimeOver` enum('true','false') NOT NULL DEFAULT 'true',
  `certificates` smallint(6) NOT NULL DEFAULT 300,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of castle
-- ----------------------------
INSERT INTO `castle` VALUES ('1', '0', '0', '0', '0', '0', '1780779600000', 'false', '300');
INSERT INTO `castle` VALUES ('2', '0', '0', '0', '0', '0', '1780779600000', 'false', '300');
INSERT INTO `castle` VALUES ('3', '0', '0', '0', '0', '0', '1780261200000', 'false', '300');
INSERT INTO `castle` VALUES ('4', '0', '0', '0', '0', '0', '1780261200000', 'false', '300');
INSERT INTO `castle` VALUES ('5', '0', '0', '0', '0', '0', '1780779600000', 'false', '300');
INSERT INTO `castle` VALUES ('6', '0', '0', '0', '0', '0', '1780261200000', 'false', '300');
INSERT INTO `castle` VALUES ('7', '0', '0', '0', '0', '0', '1780261200000', 'false', '300');
INSERT INTO `castle` VALUES ('8', '0', '0', '0', '0', '0', '1780779600000', 'false', '300');
INSERT INTO `castle` VALUES ('9', '0', '0', '0', '0', '0', '1780779600000', 'false', '300');

-- ----------------------------
-- Table structure for `castle_doorupgrade`
-- ----------------------------
DROP TABLE IF EXISTS `castle_doorupgrade`;
CREATE TABLE `castle_doorupgrade` (
  `doorId` int(11) NOT NULL DEFAULT 0,
  `hp` tinyint(4) NOT NULL DEFAULT 0,
  `castleId` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`doorId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of castle_doorupgrade
-- ----------------------------

-- ----------------------------
-- Table structure for `castle_functions`
-- ----------------------------
DROP TABLE IF EXISTS `castle_functions`;
CREATE TABLE `castle_functions` (
  `castle_id` int(2) NOT NULL DEFAULT 0,
  `type` int(1) NOT NULL DEFAULT 0,
  `lvl` int(3) NOT NULL DEFAULT 0,
  `lease` int(10) NOT NULL DEFAULT 0,
  `rate` decimal(20,0) NOT NULL DEFAULT 0,
  `endTime` bigint(13) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`castle_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of castle_functions
-- ----------------------------

-- ----------------------------
-- Table structure for `castle_manor_procure`
-- ----------------------------
DROP TABLE IF EXISTS `castle_manor_procure`;
CREATE TABLE `castle_manor_procure` (
  `castle_id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `crop_id` int(11) unsigned NOT NULL DEFAULT 0,
  `amount` int(11) unsigned NOT NULL DEFAULT 0,
  `start_amount` int(11) unsigned NOT NULL DEFAULT 0,
  `price` int(11) unsigned NOT NULL DEFAULT 0,
  `reward_type` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `next_period` tinyint(1) unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`castle_id`,`crop_id`,`next_period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of castle_manor_procure
-- ----------------------------

-- ----------------------------
-- Table structure for `castle_manor_production`
-- ----------------------------
DROP TABLE IF EXISTS `castle_manor_production`;
CREATE TABLE `castle_manor_production` (
  `castle_id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `seed_id` int(11) unsigned NOT NULL DEFAULT 0,
  `amount` int(11) unsigned NOT NULL DEFAULT 0,
  `start_amount` int(11) unsigned NOT NULL DEFAULT 0,
  `price` int(11) unsigned NOT NULL DEFAULT 0,
  `next_period` tinyint(1) unsigned NOT NULL DEFAULT 1,
  PRIMARY KEY (`castle_id`,`seed_id`,`next_period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of castle_manor_production
-- ----------------------------

-- ----------------------------
-- Table structure for `castle_trapupgrade`
-- ----------------------------
DROP TABLE IF EXISTS `castle_trapupgrade`;
CREATE TABLE `castle_trapupgrade` (
  `castleId` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `towerIndex` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `level` tinyint(3) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`towerIndex`,`castleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of castle_trapupgrade
-- ----------------------------

-- ----------------------------
-- Table structure for `characters`
-- ----------------------------
DROP TABLE IF EXISTS `characters`;
CREATE TABLE `characters` (
  `account_name` varchar(45) DEFAULT NULL,
  `obj_Id` int(10) unsigned NOT NULL DEFAULT 0,
  `char_name` varchar(35) NOT NULL,
  `level` tinyint(3) unsigned DEFAULT NULL,
  `maxHp` mediumint(8) unsigned DEFAULT NULL,
  `curHp` mediumint(8) unsigned DEFAULT NULL,
  `maxCp` mediumint(8) unsigned DEFAULT NULL,
  `curCp` mediumint(8) unsigned DEFAULT NULL,
  `maxMp` mediumint(8) unsigned DEFAULT NULL,
  `curMp` mediumint(8) unsigned DEFAULT NULL,
  `face` tinyint(3) unsigned DEFAULT NULL,
  `hairStyle` tinyint(3) unsigned DEFAULT NULL,
  `hairColor` tinyint(3) unsigned DEFAULT NULL,
  `sex` tinyint(3) unsigned DEFAULT NULL,
  `heading` mediumint(9) DEFAULT NULL,
  `x` mediumint(9) DEFAULT NULL,
  `y` mediumint(9) DEFAULT NULL,
  `z` mediumint(9) DEFAULT NULL,
  `exp` bigint(20) unsigned DEFAULT 0,
  `expBeforeDeath` bigint(20) unsigned DEFAULT 0,
  `sp` int(10) unsigned NOT NULL DEFAULT 0,
  `karma` int(10) unsigned DEFAULT NULL,
  `pvpkills` smallint(5) unsigned DEFAULT NULL,
  `pkkills` smallint(5) unsigned DEFAULT NULL,
  `clanid` int(10) unsigned DEFAULT NULL,
  `race` tinyint(3) unsigned DEFAULT NULL,
  `classid` tinyint(3) unsigned DEFAULT NULL,
  `base_class` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `deletetime` bigint(20) DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `rec_have` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `rec_left` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `accesslevel` mediumint(9) DEFAULT 0,
  `online` tinyint(3) unsigned DEFAULT NULL,
  `onlinetime` int(11) DEFAULT NULL,
  `lastAccess` bigint(20) unsigned DEFAULT NULL,
  `wantspeace` tinyint(3) unsigned DEFAULT 0,
  `isin7sdungeon` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `punish_level` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `punish_timer` bigint(20) unsigned NOT NULL DEFAULT 0,
  `power_grade` tinyint(3) unsigned DEFAULT NULL,
  `nobless` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `hero` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `subpledge` smallint(6) NOT NULL DEFAULT 0,
  `lvl_joined_academy` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `apprentice` int(10) unsigned NOT NULL DEFAULT 0,
  `sponsor` int(10) unsigned NOT NULL DEFAULT 0,
  `varka_ketra_ally` tinyint(4) NOT NULL DEFAULT 0,
  `clan_join_expiry_time` bigint(20) unsigned NOT NULL DEFAULT 0,
  `clan_create_expiry_time` bigint(20) unsigned NOT NULL DEFAULT 0,
  `death_penalty_level` smallint(5) unsigned NOT NULL DEFAULT 0,
  `herountil` bigint(20) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`obj_Id`),
  KEY `clanid` (`clanid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- ----------------------------
-- Records of characters
-- ----------------------------
INSERT INTO `characters` VALUES ('root', '268493142', 'Dev', '72', '1511', '1511', '755', '755', '1125', '1125', '0', '0', '0', '0', '46249', '82635', '148655', '-3448', '532692055', '11066012', '20234150', '77', '0', '2', '0', '1', '25', '25', '0', 'Master', '0', '9', '8', '0', '137508', '1756180914019', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('tester', '268493285', 'LevelUp', '80', '7150', '7150', '2500', '2500', '1632', '1632', '0', '0', '0', '0', '54890', '82022', '147569', '-3468', '4200000000', '0', '1008', '0', '0', '0', '0', '2', '106', '106', '0', 'Brproject', '0', '9', '0', '0', '772', '1770937286877', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('tester', '268493302', 'Premium', '18', '713', '713', '166', '166', '170', '170', '0', '0', '0', '0', '52680', '45207', '41179', '-3508', '539995', '0', '1354', '0', '0', '0', '0', '1', '18', '18', '0', 'Brproject', '0', '3', '0', '0', '1573', '1770526477912', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('astiler', '268493309', 'LeftWeb', '66', '3948', '3948', '1124', '1124', '2565', '2565', '0', '0', '0', '0', '62657', '213039', '-114514', '-1635', '285148577', '286607291', '28684596', '0', '106', '8', '0', '1', '27', '27', '0', 'Auto Farming', '0', '9', '0', '0', '128546', '1773012076117', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('dhousefe', '268493467', 'TestLouco', '20', '516', '0', '206', '206', '184', '184', '0', '0', '0', '0', '25889', '-103676', '238335', '-3552', '919262', '933591', '25143', '0', '0', '0', '0', '0', '0', '0', '0', 'Auto Farming', '0', '6', '0', '0', '53046', '1769389495065', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('testano', '268493578', 'DEstroier', '52', '4269', '4269', '2288', '2288', '771', '771', '0', '0', '0', '0', '59137', '167653', '-5783', '-2744', '57057674', '51554466', '540935', '0', '0', '0', '0', '0', '2', '2', '0', 'Brproject', '0', '9', '0', '0', '2173', '1773006992488', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('santiagoice', '268493605', 'Somelier', '47', '3714', '3714', '881', '881', '648', '639', '0', '0', '0', '0', '17766', '-21141', '78535', '-5172', '31511159', '28257634', '1364062', '0', '0', '0', '0', '1', '24', '24', '0', 'Brproject', '0', '9', '0', '0', '4197', '1771900760126', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('testando', '268493707', 'SummonBoss', '35', '1928', '1127', '433', '433', '392', '380', '0', '0', '0', '0', '14619', '75434', '-127327', '-3144', '9830653', '9469931', '539355', '0', '0', '0', '0', '1', '22', '22', '0', 'Auto Farming', '0', '6', '0', '0', '6639', '1771887427048', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('astiler', '268493809', 'Magos', '9', '224', '224', '112', '112', '177', '177', '0', '0', '0', '1', '78', '184628', '-106340', '-6304', '32579', '0', '1651', '0', '0', '0', '0', '0', '10', '10', '0', 'Brproject', '0', '3', '0', '0', '3818', '1768970323025', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('astiler', '268493876', 'Lockmove', '34', '1038', '1038', '415', '415', '470', '271', '0', '0', '0', '0', '45132', '-14177', '145495', '-3602', '8674767', '1036944', '381479', '0', '0', '0', '0', '1', '22', '22', '0', 'Auto Farming', '0', '6', '0', '0', '32319', '1770474612265', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('santiagoice', '268493896', 'LamMap', '59', '6601', '6490', '2656', '2656', '989', '975', '0', '0', '0', '1', '53988', '180847', '7013', '-2728', '120020244', '0', '4684429', '0', '0', '0', '0', '4', '55', '55', '0', 'Brproject', '0', '9', '0', '0', '9886', '1771036464644', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('santiagoice', '268493913', 'DaggerTestudo', '47', '3480', '3480', '661', '290', '648', '258', '0', '0', '0', '0', '49980', '144870', '25043', '-2140', '31964787', '32088691', '500174', '0', '1', '0', '0', '2', '36', '36', '0', 'Auto Farming', '0', '9', '0', '0', '3109', '1770957998093', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('santiagoice', '268493980', 'DarkMystco', '7', '199', '199', '90', '90', '191', '191', '0', '0', '0', '0', '57118', '17326', '13443', '-3736', '11672', '0', '855', '0', '0', '0', '0', '2', '38', '38', '0', 'Brproject', '0', '3', '0', '0', '975', '1766979308252', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('root', '268494048', 'Raider', '69', '4584', '4584', '3031', '3095', '962', '474', '0', '2', '2', '0', '2903', '81670', '149044', '-3448', '427895256', '429678190', '18997', '0', '0', '0', '0', '3', '45', '45', '0', '', '0', '9', '0', '0', '19790', '1756056483937', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('dhousefe', '268494184', 'Maelly', '16', '773', '773', '334', '334', '154', '154', '0', '0', '0', '1', '16939', '-59358', '71996', '-3268', '365080', '0', '15719', '0', '0', '0', '0', '4', '53', '53', '0', 'Auto Farming', '0', '3', '0', '0', '4459', '1771025455147', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('dhousefe', '268494228', 'Tester', '80', '4339', '1630', '1698', '1698', '4490', '74', '0', '0', '0', '1', '24211', '149595', '-112686', '-2065', '5488193896', '5509193846', '61063558', '0', '0', '0', '268494332', '1', '103', '103', '0', 'Auto Farming', '0', '9', '0', '0', '453232', '1777088273651', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '0');
INSERT INTO `characters` VALUES ('testando', '268494324', 'Warlord', '55', '3466', '3466', '2410', '2410', '854', '854', '0', '0', '0', '0', '52440', '83219', '148442', '-3405', '76880770', '77274674', '3375577', '0', '0', '0', '0', '0', '3', '3', '0', 'Auto Farming', '0', '9', '0', '0', '3962', '1771556849914', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('dhousefe', '268494356', 'DaggerTes', '60', '3533', '3533', '2480', '361', '1010', '146', '0', '0', '0', '1', '63765', '85620', '146477', '-3400', '145469768', '146307211', '796364', '0', '0', '0', '0', '1', '20', '20', '0', 'Auto Farming', '0', '9', '0', '0', '28941', '1777429848164', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('astiler', '268494406', 'Canacel', '80', '3052', '3052', '1526', '1526', '3293', '3293', '0', '0', '0', '1', '9115', '82337', '147867', '-3464', '4200000000', '1522437632', '19304126', '0', '16', '6', '0', '0', '94', '94', '0', 'Admin', '0', '9', '7', '0', '50875', '1770952756400', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('socerer', '268494667', 'Socerer', '79', '4127', '3569', '2082', '2082', '771', '623', '0', '2', '1', '1', '12450', '186304', '22078', '-3352', '2280152488', '46779095', '20906054', '0', '4', '1', '0', '0', '9', '94', '0', 'Auto Farming', '0', '9', '0', '0', '15981', '1777434077497', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('dhousefe', '268494751', 'Testuda', '76', '3913', '3913', '1531', '1531', '3896', '3604', '0', '0', '0', '1', '17558', '119286', '-37063', '-3319', '1025143345', '825547903', '105009851', '0', '45', '2', '268493856', '2', '40', '40', '0', 'Chat Moderator', '0', '9', '1', '0', '225995', '1779165739396', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('admin', '268495502', 'Dagger', '33', '826', '826', '330', '330', '300', '166', '0', '0', '0', '0', '32959', '49755', '69186', '-3520', '7519139', '0', '264933', '0', '0', '0', '0', '0', '0', '0', '0', 'Auto Farming', '0', '6', '0', '0', '133223', '1779036191569', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('astiler', '268495632', 'Chibinha', '10', '518', '518', '120', '120', '104', '25', '0', '0', '0', '0', '63727', '-75827', '254646', '-3248', '70174', '0', '4688', '0', '0', '0', '0', '0', '0', '0', '0', 'Brproject', '0', '3', '0', '0', '2144', '1768716915992', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('tester', '268495874', 'PickUP', '41', '1166', '1166', '816', '816', '386', '386', '0', '0', '0', '0', '47783', '-12585', '122776', '-3112', '17137002', '0', '1105', '0', '0', '0', '0', '4', '53', '53', '0', 'Master', '0', '9', '8', '0', '804', '1770558350116', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('dhousefe', '268497182', 'Dhousefe', '80', '4992', '4992', '1403', '1403', '2255', '2255', '1', '1', '2', '0', '51689', '152058', '-107283', '-2848', '6279556673', '6299994998', '1614306839', '0', '28', '5', '268494102', '0', '15', '15', '0', 'Master', '0', '9', '8', '0', '1701853', '1779162591847', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('dhousefe', '268497394', 'Monster', '60', '7661', '7661', '2357', '2357', '1018', '1018', '0', '2', '0', '0', '37261', '185440', '20891', '-3265', '127788328', '128470956', '12858393', '0', '8', '1', '0', '3', '46', '46', '0', 'Auto Farming', '0', '9', '0', '0', '248974', '1777433628228', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('santiagoice', '268497927', 'DarkTestar', '80', '5183', '4043', '1535', '1535', '2204', '1759', '0', '0', '0', '1', '25098', '81990', '147567', '-3469', '4200000000', '0', '1205', '0', '0', '0', '0', '2', '108', '108', '0', 'RUSaCis', '0', '9', '0', '0', '685', '1770937287725', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('jogar', '268498074', 'Arqueira', '37', '1309', '1129', '523', '523', '415', '392', '1', '2', '1', '1', '44536', '-42222', '215867', '-5080', '11418070', '0', '506358', '0', '0', '0', '0', '0', '7', '7', '0', 'Auto Farming', '0', '6', '0', '0', '4761', '1776125771615', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('testnado', '268498107', 'Demolidor', '46', '1852', '1852', '527', '527', '923', '923', '2', '3', '2', '0', '3947', '-85879', '-185590', '-10054', '28049509', '0', '50034', '0', '0', '0', '0', '3', '49', '49', '0', 'Brproject', '0', '9', '0', '0', '1778', '1777073249804', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('testano', '268498118', 'archer', '20', '779', '779', '181', '181', '187', '187', '0', '0', '0', '0', '29014', '-42496', '207649', '-3154', '835854', '0', '24020', '0', '0', '0', '0', '1', '22', '22', '0', 'Brproject', '0', '6', '0', '0', '1772', '1773971530591', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('testano', '268498467', 'Catadora', '51', '4613', '4613', '1854', '1854', '645', '645', '0', '0', '0', '1', '57344', '-17470', '180966', '-5520', '50570647', '45561743', '2863888', '0', '0', '0', '0', '4', '54', '54', '0', 'Auto Farming', '0', '9', '0', '0', '331290', '1773439937280', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('testano', '268503745', 'Clerig', '31', '799', '799', '399', '399', '667', '667', '0', '2', '2', '1', '19449', '-44777', '203514', '-3594', '5492099', '5538994', '300974', '0', '0', '0', '0', '0', '15', '15', '0', 'Auto Farming', '0', '6', '0', '0', '36214', '1773971581064', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '2', '0');
INSERT INTO `characters` VALUES ('testnado', '268504379', 'Aguaneles', '50', '1941', '1941', '718', '718', '2271', '2271', '0', '3', '2', '1', '31942', '117009', '76909', '-2704', '40153995', '0', '1111945', '0', '0', '0', '0', '1', '27', '27', '0', 'Agua', '0', '9', '0', '0', '5699', '1777078982301', '0', '0', '0', '0', '0', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('astiler', '268509776', 'Lambda', '48', '4906', '4778', '2069', '2069', '721', '687', '1', '1', '1', '0', '33512', '182617', '17462', '-3168', '36155437', '35089633', '1517214', '0', '0', '0', '268510534', '4', '55', '55', '0', 'Auto Farming', '0', '9', '0', '0', '7980', '1770699072608', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('astiler', '268523291', 'Autospoil', '46', '4152', '4152', '1866', '1866', '626', '626', '0', '0', '0', '1', '12567', '84874', '116418', '-3056', '30061534', '0', '1770960', '0', '1', '0', '0', '4', '57', '57', '0', 'Auto Farming', '0', '9', '0', '0', '14496', '1770665052180', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('santiagoice', '268543578', 'MagaDark', '41', '1347', '1347', '499', '499', '1126', '1126', '1', '3', '1', '1', '33775', '17957', '145529', '-3080', '18438288', '18531217', '1183671', '0', '0', '0', '0', '2', '39', '39', '0', 'Brproject', '0', '9', '0', '0', '69779', '1771446516491', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `characters` VALUES ('santiagoice', '268631868', 'DUelista', '79', '8627', '8110', '4855', '4855', '1575', '1444', '1', '1', '1', '1', '53000', '113672', '-42827', '-2720', '2101596046', '132010158', '8395426', '0', '0', '0', '0', '0', '88', '88', '0', 'Auto Farming', '0', '9', '0', '0', '68698', '1771905766441', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for `character_data`
-- ----------------------------
DROP TABLE IF EXISTS `character_data`;
CREATE TABLE `character_data` (
  `charId` int(11) NOT NULL,
  `valueName` varchar(32) NOT NULL,
  `valueData` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`charId`,`valueName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- ----------------------------
-- Records of character_data
-- ----------------------------
INSERT INTO `character_data` VALUES ('268497182', 'buffProtected', 'true');
INSERT INTO `character_data` VALUES ('268498467', 'locale', 'en-US');

-- ----------------------------
-- Table structure for `character_hennas`
-- ----------------------------
DROP TABLE IF EXISTS `character_hennas`;
CREATE TABLE `character_hennas` (
  `char_obj_id` int(11) NOT NULL DEFAULT 0,
  `symbol_id` int(11) DEFAULT NULL,
  `slot` int(11) NOT NULL DEFAULT 0,
  `class_index` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`slot`,`class_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_hennas
-- ----------------------------
INSERT INTO `character_hennas` VALUES ('268493309', '180', '1', '0');
INSERT INTO `character_hennas` VALUES ('268493309', '175', '2', '0');
INSERT INTO `character_hennas` VALUES ('268494228', '175', '1', '0');
INSERT INTO `character_hennas` VALUES ('268494228', '180', '2', '0');
INSERT INTO `character_hennas` VALUES ('268494667', '120', '1', '0');
INSERT INTO `character_hennas` VALUES ('268494667', '174', '1', '1');
INSERT INTO `character_hennas` VALUES ('268494667', '169', '1', '2');
INSERT INTO `character_hennas` VALUES ('268494667', '175', '2', '0');
INSERT INTO `character_hennas` VALUES ('268494667', '174', '2', '2');
INSERT INTO `character_hennas` VALUES ('268494667', '180', '3', '0');
INSERT INTO `character_hennas` VALUES ('268494751', '180', '1', '0');
INSERT INTO `character_hennas` VALUES ('268494751', '163', '2', '0');
INSERT INTO `character_hennas` VALUES ('268497394', '157', '1', '0');
INSERT INTO `character_hennas` VALUES ('268497394', '174', '2', '0');

-- ----------------------------
-- Table structure for `character_macroses`
-- ----------------------------
DROP TABLE IF EXISTS `character_macroses`;
CREATE TABLE `character_macroses` (
  `char_obj_id` int(11) NOT NULL DEFAULT 0,
  `id` int(11) NOT NULL DEFAULT 0,
  `icon` int(11) DEFAULT NULL,
  `name` varchar(40) DEFAULT NULL,
  `descr` varchar(80) DEFAULT NULL,
  `acronym` varchar(4) DEFAULT NULL,
  `commands` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`char_obj_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_macroses
-- ----------------------------
INSERT INTO `character_macroses` VALUES ('268493142', '1000', '0', 'kill', '1', '', '3,0,0,//kill;');
INSERT INTO `character_macroses` VALUES ('268493142', '1001', '0', 'ad', '1', '', '3,0,0,//admin;');
INSERT INTO `character_macroses` VALUES ('268493285', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493285', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493285', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493285', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493285', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493285', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493285', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493285', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493302', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493302', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493302', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493302', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493302', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493302', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493302', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493302', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493309', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493309', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493309', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493309', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493309', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493309', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493309', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493309', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493309', '1000', '65', 'Autofarm', '', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268493578', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493578', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493578', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493578', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493578', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493578', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493578', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493578', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493605', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493605', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493605', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493605', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493605', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493605', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493605', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493605', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493707', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493707', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493707', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493707', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493707', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493707', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493707', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493707', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493809', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493809', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493809', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493809', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493809', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493809', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493809', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493809', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493876', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493876', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493876', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493876', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493876', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493876', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493876', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493876', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493896', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493896', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493896', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493896', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493896', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493896', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493896', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493896', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493896', '1000', '17', 'Autofarm', '', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268493913', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493913', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493913', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493913', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493913', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493913', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493913', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493913', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268493980', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268493980', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268493980', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268493980', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268493980', '104', '0', 'Skil', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268493980', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268493980', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268493980', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268494228', '1000', '0', 'kill', '1', '', '3,0,0,//kill;');
INSERT INTO `character_macroses` VALUES ('268494228', '1001', '4', 'adm', '5', '', '3,0,0,//admin;');
INSERT INTO `character_macroses` VALUES ('268494228', '1002', '85', 'auto', '', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268494324', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268494324', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268494324', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268494324', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268494324', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268494324', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268494324', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268494324', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268494356', '1000', '68', 'Auto', '69', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268494406', '1000', '0', 'asd', '1', '', '3,0,0,//admin;');
INSERT INTO `character_macroses` VALUES ('268494667', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268494667', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268494667', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268494667', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268494667', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268494667', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268494667', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268494667', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268494667', '1000', '71', 'autofarm', '', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268494751', '1000', '0', 'kill', '1', '', '3,0,0,//kill;');
INSERT INTO `character_macroses` VALUES ('268494751', '1001', '1', 'adm', '', '', '3,0,0,//admin;');
INSERT INTO `character_macroses` VALUES ('268494751', '1002', '65', 'autofarm', '', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268494751', '1003', '16', 'TOrnament', '17', '', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268495502', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268495502', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268495502', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268495502', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268495502', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268495502', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268495502', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268495502', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268495632', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268495632', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268495632', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268495632', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268495632', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268495632', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268495632', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268495632', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268495874', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268495874', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268495874', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268495874', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268495874', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268495874', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268495874', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268495874', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268497182', '1000', '70', 'admin', '71', '', '3,0,0,//admin;');
INSERT INTO `character_macroses` VALUES ('268497182', '1001', '8', 'Zone', '9', '', '3,0,0,//zone;');
INSERT INTO `character_macroses` VALUES ('268497182', '1002', '15', 'kill', '16', '', '3,0,0,//kill;');
INSERT INTO `character_macroses` VALUES ('268497182', '1003', '74', 'Mail', '75', '', '3,0,0,.email;');
INSERT INTO `character_macroses` VALUES ('268497182', '1004', '0', 'html', '1', '', '3,0,0,//reload html;');
INSERT INTO `character_macroses` VALUES ('268497182', '1005', '46', 'movi', '47', '', '3,0,0,//move_debug;');
INSERT INTO `character_macroses` VALUES ('268497182', '1006', '0', 'testemov', '1', '', '3,0,0,//move_test;');
INSERT INTO `character_macroses` VALUES ('268497182', '1007', '0', 'movecha', '1', '', '3,0,0,//move_cache;');
INSERT INTO `character_macroses` VALUES ('268497182', '1008', '0', 'dbg', '1', '', '3,0,0,//move_dbg;');
INSERT INTO `character_macroses` VALUES ('268497182', '1009', '0', 'patch', '1', '', '3,0,0,//path_dbg;');
INSERT INTO `character_macroses` VALUES ('268497182', '1010', '48', 'geogrid', '49', '', '3,0,0,//geo grid 30;');
INSERT INTO `character_macroses` VALUES ('268497182', '1011', '39', 'delete', '40', '', '3,0,0,//delete;');
INSERT INTO `character_macroses` VALUES ('268497182', '1012', '32', 'NPC Reload', '33', '', '3,0,0,//reload npc;');
INSERT INTO `character_macroses` VALUES ('268497182', '1013', '38', 'spawn 55', '39', '', '3,0,0,//spawn 55033;');
INSERT INTO `character_macroses` VALUES ('268497394', '1000', '94', 'autofarm', '', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268497927', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268497927', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268497927', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268497927', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268498074', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268498074', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268498074', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268498074', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268498074', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268498074', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268498074', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268498074', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268498107', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268498107', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268498107', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268498107', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268498107', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268498107', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268498107', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268498107', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268498107', '1000', '66', 'auto', 'Autofarm', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268498118', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268498118', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268498118', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268498118', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268498118', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268498118', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268498118', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268498118', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268498467', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268498467', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268498467', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268498467', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268498467', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268498467', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268498467', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268498467', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268503745', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268503745', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268503745', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268503745', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268503745', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268503745', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268503745', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268503745', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268504379', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268504379', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268504379', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268504379', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268504379', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268504379', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268504379', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268504379', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268504379', '1000', '66', 'Auto', '', '', '3,0,0,.autofarm;');
INSERT INTO `character_macroses` VALUES ('268509776', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268509776', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268509776', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268509776', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268509776', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268509776', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268509776', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268509776', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268523291', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268523291', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268523291', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268523291', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268523291', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268523291', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268523291', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268523291', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268543578', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268543578', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268543578', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268543578', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268543578', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268543578', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268543578', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268543578', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');
INSERT INTO `character_macroses` VALUES ('268631868', '100', '0', 'Menu', 'Open player settings panel', 'MENU', '3,0,0,.menu;');
INSERT INTO `character_macroses` VALUES ('268631868', '101', '0', 'Premium', 'Check Premium status of PA', 'PREM', '3,0,0,.premium;');
INSERT INTO `character_macroses` VALUES ('268631868', '102', '0', 'Raid', 'Check Raid Boss status', 'RAID', '3,0,0,.raid;');
INSERT INTO `character_macroses` VALUES ('268631868', '103', '0', 'Epic', 'Check Epic Boss status', 'EPIC', '3,0,0,.epic;');
INSERT INTO `character_macroses` VALUES ('268631868', '104', '0', 'Skin', 'Manage Skin', 'SKIN', '3,0,0,.skin;');
INSERT INTO `character_macroses` VALUES ('268631868', '105', '0', 'Tournament', 'Tournament Lobby', 'TOUR', '3,0,0,.tour;');
INSERT INTO `character_macroses` VALUES ('268631868', '106', '0', 'Remove Buff', 'Remove Buff', 'REM', '3,0,0,.rembuff;');
INSERT INTO `character_macroses` VALUES ('268631868', '107', '0', 'Manage Roulette', 'Roulette', 'ROUL', '3,0,0,.roulette;');

-- ----------------------------
-- Table structure for `character_memo`
-- ----------------------------
DROP TABLE IF EXISTS `character_memo`;
CREATE TABLE `character_memo` (
  `charId` int(10) unsigned NOT NULL,
  `var` varchar(255) NOT NULL,
  `val` text NOT NULL,
  PRIMARY KEY (`charId`,`var`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_memo
-- ----------------------------
INSERT INTO `character_memo` VALUES ('268493285', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493285', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493285', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493302', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493302', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493302', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493309', 'agation', '9265');
INSERT INTO `character_memo` VALUES ('268493309', 'dressme_armor', '9102');
INSERT INTO `character_memo` VALUES ('268493309', 'dressme_weapon', '9203');
INSERT INTO `character_memo` VALUES ('268493309', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493309', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493309', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493467', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493467', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493467', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493578', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493578', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493578', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493605', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493605', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493605', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493707', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493707', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493707', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493809', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493809', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493809', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493876', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493876', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493876', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493896', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493896', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493896', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493913', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493913', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493913', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268493980', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268493980', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268493980', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268494184', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268494184', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268494184', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268494228', 'agation', '9579');
INSERT INTO `character_memo` VALUES ('268494324', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268494324', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268494324', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268494356', 'MissQueen_Trainees', 'true');
INSERT INTO `character_memo` VALUES ('268494356', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268494356', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268494356', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268494406', 'agation', '9581');
INSERT INTO `character_memo` VALUES ('268494406', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268494406', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268494406', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268494667', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268494667', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268494667', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268494751', 'dressme_armor', '9102');
INSERT INTO `character_memo` VALUES ('268494751', 'dressme_weapon', '9203');
INSERT INTO `character_memo` VALUES ('268495502', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268495502', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268495502', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268495632', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268495632', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268495632', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268495874', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268495874', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268495874', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268497182', 'agation', '9266');
INSERT INTO `character_memo` VALUES ('268497182', 'dressme_armor', '9102');
INSERT INTO `character_memo` VALUES ('268497182', 'dressme_weapon', '9210');
INSERT INTO `character_memo` VALUES ('268497394', 'agation', '9274');
INSERT INTO `character_memo` VALUES ('268497394', 'PlayerGodTimeLeft', '600');
INSERT INTO `character_memo` VALUES ('268497927', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268497927', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268497927', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268498074', 'dressme_armor', '9101');
INSERT INTO `character_memo` VALUES ('268498074', 'dressme_weapon', '9211');
INSERT INTO `character_memo` VALUES ('268498074', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268498074', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268498074', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268498107', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268498107', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268498107', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268498118', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268498118', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268498118', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268498467', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268498467', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268498467', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268503745', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268503745', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268503745', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268504379', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268504379', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268504379', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268509776', 'MissQueen_Trainees', 'true');
INSERT INTO `character_memo` VALUES ('268509776', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268509776', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268509776', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268523291', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268523291', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268523291', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268543578', 'dressme_armor', '9102');
INSERT INTO `character_memo` VALUES ('268543578', 'dressme_weapon', '9208');
INSERT INTO `character_memo` VALUES ('268543578', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268543578', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268543578', 'quest_notify_html', 'false');
INSERT INTO `character_memo` VALUES ('268631868', 'quest_1_completed', 'false');
INSERT INTO `character_memo` VALUES ('268631868', 'quest_notify_chat', 'false');
INSERT INTO `character_memo` VALUES ('268631868', 'quest_notify_html', 'false');

-- ----------------------------
-- Table structure for `character_mission`
-- ----------------------------
DROP TABLE IF EXISTS `character_mission`;
CREATE TABLE `character_mission` (
  `object_id` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `level` int(11) NOT NULL DEFAULT 0,
  `value` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`object_id`,`type`),
  KEY `idx_object_id` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_mission
-- ----------------------------
INSERT INTO `character_mission` VALUES ('268493142', 'DEATHS', '0', '3');
INSERT INTO `character_mission` VALUES ('268493142', 'KARMA', '0', '360');
INSERT INTO `character_mission` VALUES ('268493142', 'MONSTER', '0', '29');
INSERT INTO `character_mission` VALUES ('268493142', 'PK', '0', '3');
INSERT INTO `character_mission` VALUES ('268493285', 'MONSTER', '0', '34');
INSERT INTO `character_mission` VALUES ('268493285', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268493302', 'MONSTER', '0', '398');
INSERT INTO `character_mission` VALUES ('268493309', 'DEATHS', '1', '246');
INSERT INTO `character_mission` VALUES ('268493309', 'KARMA', '0', '1080');
INSERT INTO `character_mission` VALUES ('268493309', 'MONSTER', '2', '2206');
INSERT INTO `character_mission` VALUES ('268493309', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268493309', 'PK', '0', '9');
INSERT INTO `character_mission` VALUES ('268493309', 'PVP', '1', '6');
INSERT INTO `character_mission` VALUES ('268493467', 'DEATHS', '0', '3');
INSERT INTO `character_mission` VALUES ('268493467', 'MONSTER', '0', '123');
INSERT INTO `character_mission` VALUES ('268493578', 'DEATHS', '0', '5');
INSERT INTO `character_mission` VALUES ('268493578', 'MONSTER', '0', '254');
INSERT INTO `character_mission` VALUES ('268493578', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268493605', 'DEATHS', '0', '2');
INSERT INTO `character_mission` VALUES ('268493605', 'MONSTER', '1', '90');
INSERT INTO `character_mission` VALUES ('268493605', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268493707', 'DEATHS', '0', '5');
INSERT INTO `character_mission` VALUES ('268493707', 'MONSTER', '1', '60');
INSERT INTO `character_mission` VALUES ('268493707', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268493809', 'MONSTER', '0', '64');
INSERT INTO `character_mission` VALUES ('268493876', 'DEATHS', '0', '9');
INSERT INTO `character_mission` VALUES ('268493876', 'MONSTER', '1', '69');
INSERT INTO `character_mission` VALUES ('268493896', 'MONSTER', '1', '117');
INSERT INTO `character_mission` VALUES ('268493896', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268493896', 'SPOIL', '0', '475');
INSERT INTO `character_mission` VALUES ('268493913', 'DEATHS', '0', '5');
INSERT INTO `character_mission` VALUES ('268493913', 'MONSTER', '0', '151');
INSERT INTO `character_mission` VALUES ('268493913', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268493913', 'PVP', '0', '2');
INSERT INTO `character_mission` VALUES ('268493980', 'MONSTER', '0', '28');
INSERT INTO `character_mission` VALUES ('268494048', 'DEATHS', '0', '3');
INSERT INTO `character_mission` VALUES ('268494048', 'MONSTER', '0', '88');
INSERT INTO `character_mission` VALUES ('268494184', 'DEATHS', '0', '3');
INSERT INTO `character_mission` VALUES ('268494184', 'MONSTER', '0', '139');
INSERT INTO `character_mission` VALUES ('268494184', 'SPOIL', '0', '17');
INSERT INTO `character_mission` VALUES ('268494228', 'DEATHS', '0', '82');
INSERT INTO `character_mission` VALUES ('268494228', 'ENCHANT_SUCCESS', '0', '4');
INSERT INTO `character_mission` VALUES ('268494228', 'ENCHANT_WEAPON', '0', '3');
INSERT INTO `character_mission` VALUES ('268494228', 'KARMA', '1', '5000');
INSERT INTO `character_mission` VALUES ('268494228', 'MONSTER', '2', '1394');
INSERT INTO `character_mission` VALUES ('268494228', 'RAIDBOSS', '0', '8');
INSERT INTO `character_mission` VALUES ('268494324', 'DEATHS', '0', '3');
INSERT INTO `character_mission` VALUES ('268494324', 'MONSTER', '0', '424');
INSERT INTO `character_mission` VALUES ('268494324', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268494356', 'DEATHS', '0', '9');
INSERT INTO `character_mission` VALUES ('268494356', 'MONSTER', '1', '89');
INSERT INTO `character_mission` VALUES ('268494356', 'QUEST_COMPLETE', '0', '2');
INSERT INTO `character_mission` VALUES ('268494406', 'DEATHS', '0', '23');
INSERT INTO `character_mission` VALUES ('268494406', 'KARMA', '0', '3000');
INSERT INTO `character_mission` VALUES ('268494406', 'MONSTER', '1', '355');
INSERT INTO `character_mission` VALUES ('268494406', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268494406', 'OLYMPIAD_FIGHT', '1', '4');
INSERT INTO `character_mission` VALUES ('268494406', 'OLYMPIAD_FIGHT_LOST', '1', '2');
INSERT INTO `character_mission` VALUES ('268494406', 'OLYMPIAD_FIGHT_WON', '0', '3');
INSERT INTO `character_mission` VALUES ('268494406', 'PK', '0', '8');
INSERT INTO `character_mission` VALUES ('268494406', 'PVP', '0', '17');
INSERT INTO `character_mission` VALUES ('268494406', 'RAIDBOSS', '0', '2');
INSERT INTO `character_mission` VALUES ('268494667', 'DEATHS', '0', '5');
INSERT INTO `character_mission` VALUES ('268494667', 'ENCHANT_FAILED', '0', '3');
INSERT INTO `character_mission` VALUES ('268494667', 'ENCHANT_OTHER', '0', '2');
INSERT INTO `character_mission` VALUES ('268494667', 'ENCHANT_SUCCESS', '0', '10');
INSERT INTO `character_mission` VALUES ('268494667', 'ENCHANT_WEAPON', '1', '4');
INSERT INTO `character_mission` VALUES ('268494667', 'KARMA', '0', '240');
INSERT INTO `character_mission` VALUES ('268494667', 'MONSTER', '1', '693');
INSERT INTO `character_mission` VALUES ('268494667', 'PK', '0', '2');
INSERT INTO `character_mission` VALUES ('268494667', 'PVP', '0', '5');
INSERT INTO `character_mission` VALUES ('268494667', 'SUBCLASS', '2', '2');
INSERT INTO `character_mission` VALUES ('268494751', 'DEATHS', '3', '634');
INSERT INTO `character_mission` VALUES ('268494751', 'KARMA', '0', '360');
INSERT INTO `character_mission` VALUES ('268494751', 'MONSTER', '3', '4462');
INSERT INTO `character_mission` VALUES ('268494751', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268494751', 'PK', '0', '3');
INSERT INTO `character_mission` VALUES ('268494751', 'PVP', '0', '46');
INSERT INTO `character_mission` VALUES ('268494751', 'RAIDBOSS', '0', '2');
INSERT INTO `character_mission` VALUES ('268495502', 'MONSTER', '0', '154');
INSERT INTO `character_mission` VALUES ('268495502', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268495632', 'MONSTER', '0', '155');
INSERT INTO `character_mission` VALUES ('268495874', 'DEATHS', '0', '2');
INSERT INTO `character_mission` VALUES ('268495874', 'MONSTER', '0', '38');
INSERT INTO `character_mission` VALUES ('268497182', 'ANTHARAS', '1', '0');
INSERT INTO `character_mission` VALUES ('268497182', 'DEATHS', '0', '39');
INSERT INTO `character_mission` VALUES ('268497182', 'ENCHANT_OTHER', '0', '1');
INSERT INTO `character_mission` VALUES ('268497182', 'ENCHANT_SUCCESS', '0', '2');
INSERT INTO `character_mission` VALUES ('268497182', 'GRANDBOSS', '1', '2');
INSERT INTO `character_mission` VALUES ('268497182', 'HALISHA', '1', '0');
INSERT INTO `character_mission` VALUES ('268497182', 'KARMA', '0', '720');
INSERT INTO `character_mission` VALUES ('268497182', 'MONSTER', '5', '20134');
INSERT INTO `character_mission` VALUES ('268497182', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268497182', 'OLYMPIAD_FIGHT', '1', '4');
INSERT INTO `character_mission` VALUES ('268497182', 'OLYMPIAD_FIGHT_LOST', '0', '3');
INSERT INTO `character_mission` VALUES ('268497182', 'OLYMPIAD_FIGHT_WON', '1', '2');
INSERT INTO `character_mission` VALUES ('268497182', 'PK', '0', '5');
INSERT INTO `character_mission` VALUES ('268497182', 'PVP', '0', '28');
INSERT INTO `character_mission` VALUES ('268497182', 'QUEEN_ANT', '1', '0');
INSERT INTO `character_mission` VALUES ('268497182', 'RAIDBOSS', '1', '12');
INSERT INTO `character_mission` VALUES ('268497182', 'SPOIL', '0', '35');
INSERT INTO `character_mission` VALUES ('268497182', 'TYRANNOSAURUS', '1', '0');
INSERT INTO `character_mission` VALUES ('268497182', 'VALAKAS', '1', '0');
INSERT INTO `character_mission` VALUES ('268497182', 'ZAKEN', '1', '0');
INSERT INTO `character_mission` VALUES ('268497394', 'DEATHS', '0', '13');
INSERT INTO `character_mission` VALUES ('268497394', 'ENCHANT_SUCCESS', '0', '2');
INSERT INTO `character_mission` VALUES ('268497394', 'ENCHANT_WEAPON', '0', '1');
INSERT INTO `character_mission` VALUES ('268497394', 'KARMA', '0', '240');
INSERT INTO `character_mission` VALUES ('268497394', 'MONSTER', '1', '785');
INSERT INTO `character_mission` VALUES ('268497394', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268497394', 'PK', '0', '2');
INSERT INTO `character_mission` VALUES ('268497394', 'PVP', '0', '9');
INSERT INTO `character_mission` VALUES ('268497394', 'RAIDBOSS', '0', '3');
INSERT INTO `character_mission` VALUES ('268497927', 'MONSTER', '0', '44');
INSERT INTO `character_mission` VALUES ('268497927', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268498074', 'MONSTER', '0', '203');
INSERT INTO `character_mission` VALUES ('268498107', 'MONSTER', '0', '67');
INSERT INTO `character_mission` VALUES ('268498118', 'MONSTER', '0', '67');
INSERT INTO `character_mission` VALUES ('268498118', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268498118', 'QUEST_COMPLETE', '0', '2');
INSERT INTO `character_mission` VALUES ('268498467', 'DEATHS', '0', '5');
INSERT INTO `character_mission` VALUES ('268498467', 'MONSTER', '1', '71');
INSERT INTO `character_mission` VALUES ('268498467', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268498467', 'RECIPE_LEARN', '1', '0');
INSERT INTO `character_mission` VALUES ('268498467', 'SPOIL', '2', '823');
INSERT INTO `character_mission` VALUES ('268503745', 'DEATHS', '0', '41');
INSERT INTO `character_mission` VALUES ('268503745', 'MONSTER', '0', '443');
INSERT INTO `character_mission` VALUES ('268503745', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268504379', 'MONSTER', '1', '52');
INSERT INTO `character_mission` VALUES ('268504379', 'NOBLE', '1', '0');
INSERT INTO `character_mission` VALUES ('268504379', 'QUEST_COMPLETE', '0', '4');
INSERT INTO `character_mission` VALUES ('268509776', 'DEATHS', '0', '2');
INSERT INTO `character_mission` VALUES ('268509776', 'ENCHANT_OTHER', '0', '3');
INSERT INTO `character_mission` VALUES ('268509776', 'ENCHANT_SUCCESS', '0', '37');
INSERT INTO `character_mission` VALUES ('268509776', 'ENCHANT_WEAPON', '0', '3');
INSERT INTO `character_mission` VALUES ('268509776', 'MONSTER', '0', '316');
INSERT INTO `character_mission` VALUES ('268509776', 'SPOIL', '0', '214');
INSERT INTO `character_mission` VALUES ('268523291', 'DEATHS', '0', '2');
INSERT INTO `character_mission` VALUES ('268523291', 'MONSTER', '1', '656');
INSERT INTO `character_mission` VALUES ('268523291', 'PVP', '0', '2');
INSERT INTO `character_mission` VALUES ('268523291', 'SPOIL', '1', '77');
INSERT INTO `character_mission` VALUES ('268543578', 'DEATHS', '0', '18');
INSERT INTO `character_mission` VALUES ('268543578', 'MONSTER', '1', '124');
INSERT INTO `character_mission` VALUES ('268631868', 'DEATHS', '0', '2');
INSERT INTO `character_mission` VALUES ('268631868', 'MONSTER', '1', '914');

-- ----------------------------
-- Table structure for `character_offline_trade`
-- ----------------------------
DROP TABLE IF EXISTS `character_offline_trade`;
CREATE TABLE `character_offline_trade` (
  `charId` int(10) unsigned NOT NULL,
  `time` bigint(13) unsigned NOT NULL DEFAULT 0,
  `type` tinyint(4) NOT NULL DEFAULT 0,
  `title` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`charId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- ----------------------------
-- Records of character_offline_trade
-- ----------------------------
INSERT INTO `character_offline_trade` VALUES ('268436113', '0', '5', 'Buff Hierophant!');
INSERT INTO `character_offline_trade` VALUES ('268442483', '0', '5', 'Buff Arcana Lord Lv80!');
INSERT INTO `character_offline_trade` VALUES ('268436100', '0', '5', 'Buff Spectral Master Lv80!');
INSERT INTO `character_offline_trade` VALUES ('268447460', '0', '5', 'Buff Spectral Dancer!');
INSERT INTO `character_offline_trade` VALUES ('268464372', '0', '5', 'Buff Elemental Master Lv80!');
INSERT INTO `character_offline_trade` VALUES ('268476645', '0', '5', 'Buffs Fighter Lv85');
INSERT INTO `character_offline_trade` VALUES ('268436087', '0', '5', 'Buff Dominator Lv80!');
INSERT INTO `character_offline_trade` VALUES ('268436074', '0', '5', 'Buffs Eva\'s Saint Lv78');
INSERT INTO `character_offline_trade` VALUES ('268462570', '0', '5', 'Buff Shillien Saint Lv79!');
INSERT INTO `character_offline_trade` VALUES ('268436061', '0', '5', 'Buff Sword Muse!');
INSERT INTO `character_offline_trade` VALUES ('268436127', '0', '5', 'Buff Doom Cryer Lv80!');

-- ----------------------------
-- Table structure for `character_offline_trade_items`
-- ----------------------------
DROP TABLE IF EXISTS `character_offline_trade_items`;
CREATE TABLE `character_offline_trade_items` (
  `charId` int(10) unsigned NOT NULL,
  `item` int(10) unsigned NOT NULL DEFAULT 0,
  `count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `price` bigint(20) unsigned NOT NULL DEFAULT 0,
  `enchant` bigint(20) unsigned NOT NULL DEFAULT 0,
  KEY `charId` (`charId`),
  KEY `item` (`item`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- ----------------------------
-- Records of character_offline_trade_items
-- ----------------------------

-- ----------------------------
-- Table structure for `character_quests`
-- ----------------------------
DROP TABLE IF EXISTS `character_quests`;
CREATE TABLE `character_quests` (
  `charId` int(10) unsigned NOT NULL DEFAULT 0,
  `name` varchar(40) NOT NULL DEFAULT '',
  `var` varchar(20) NOT NULL DEFAULT '',
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`charId`,`name`,`var`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_quests
-- ----------------------------
INSERT INTO `character_quests` VALUES ('268493285', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493285', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268493285', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493285', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493285', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268493285', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493285', 'Tutorial', 'lvl', '15');
INSERT INTO `character_quests` VALUES ('268493285', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493285', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268493302', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493302', 'NewbieHelper', 'step', '1');
INSERT INTO `character_quests` VALUES ('268493302', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493302', 'Tutorial', 'Ex', '2');
INSERT INTO `character_quests` VALUES ('268493302', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268493302', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493302', 'Tutorial', 'lvl', '15');
INSERT INTO `character_quests` VALUES ('268493302', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493302', 'Tutorial', 'ucMemo', '2');
INSERT INTO `character_quests` VALUES ('268493309', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493309', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268493309', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493309', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493309', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268493309', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493309', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268493309', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493309', 'Tutorial', 'ucMemo', '5');
INSERT INTO `character_quests` VALUES ('268493467', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493467', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268493467', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493467', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268493467', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493467', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268493467', 'Tutorial', 'lvl', '19');
INSERT INTO `character_quests` VALUES ('268493467', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268493578', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493578', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268493578', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493578', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493578', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493578', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268493578', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493578', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268493605', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493605', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268493605', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493605', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493605', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493605', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268493605', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493605', 'Tutorial', 'ucMemo', '5');
INSERT INTO `character_quests` VALUES ('268493707', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493707', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493707', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268493809', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493809', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268493809', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493809', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493809', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493809', 'Tutorial', 'lvl', '7');
INSERT INTO `character_quests` VALUES ('268493809', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493809', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268493876', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493876', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268493876', 'Q160_NerupasRequest', '<cond>', '1');
INSERT INTO `character_quests` VALUES ('268493876', 'Q160_NerupasRequest', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493876', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493876', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493876', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268493876', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493876', 'Tutorial', 'lvl', '15');
INSERT INTO `character_quests` VALUES ('268493876', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493876', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268493896', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493896', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268493896', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493896', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493896', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268493896', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493896', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268493896', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493896', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268493913', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493913', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268493913', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493913', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493913', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493913', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268493913', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493913', 'Tutorial', 'ucMemo', '5');
INSERT INTO `character_quests` VALUES ('268493980', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493980', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268493980', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268493980', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268493980', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268493980', 'Tutorial', 'lvl', '7');
INSERT INTO `character_quests` VALUES ('268493980', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268493980', 'Tutorial', 'ucMemo', '5');
INSERT INTO `character_quests` VALUES ('268494048', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494048', 'Tutorial', 'Ex', '-5');
INSERT INTO `character_quests` VALUES ('268494048', 'Tutorial', 'ucMemo', '1');
INSERT INTO `character_quests` VALUES ('268494184', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494184', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'Adena', '1');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'lvl', '15');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268494184', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268494324', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494324', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268494324', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494324', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268494324', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268494324', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268494324', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268494324', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268494356', 'NewbieHelper', '<state>', 'COMPLETED');
INSERT INTO `character_quests` VALUES ('268494356', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', 'sit', '1');
INSERT INTO `character_quests` VALUES ('268494356', 'Tutorial', 'ucMemo', '5');
INSERT INTO `character_quests` VALUES ('268494406', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494406', 'Tutorial', 'Ex', '-5');
INSERT INTO `character_quests` VALUES ('268494406', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268494406', 'Tutorial', 'ucMemo', '2');
INSERT INTO `character_quests` VALUES ('268494667', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494667', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', 'lvl', '75');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268494667', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268494751', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268494751', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268494751', 'Tutorial', 'Ex', '-5');
INSERT INTO `character_quests` VALUES ('268494751', 'Tutorial', 'lvl', '75');
INSERT INTO `character_quests` VALUES ('268494751', 'Tutorial', 'ucMemo', '2');
INSERT INTO `character_quests` VALUES ('268495502', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268495502', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268495502', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268495502', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268495502', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268495502', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268495502', 'Tutorial', 'lvl', '19');
INSERT INTO `character_quests` VALUES ('268495502', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268495502', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268495632', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268495632', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268495632', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268495632', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268495632', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268495632', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268495632', 'Tutorial', 'lvl', '9');
INSERT INTO `character_quests` VALUES ('268495632', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268495632', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268495874', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268495874', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', 'lvl', '6');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268495874', 'Tutorial', 'ucMemo', '5');
INSERT INTO `character_quests` VALUES ('268497182', 'Q013_ParcelDelivery', '<cond>', '1');
INSERT INTO `character_quests` VALUES ('268497182', 'Q013_ParcelDelivery', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268497182', 'Q618_IntoTheFlame', '<cond>', '1');
INSERT INTO `character_quests` VALUES ('268497182', 'Q618_IntoTheFlame', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268497394', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268497394', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268497394', 'Q362_BardsMandolin', '<cond>', '1');
INSERT INTO `character_quests` VALUES ('268497394', 'Q362_BardsMandolin', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268497394', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268497394', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268497394', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268497394', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268497394', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268497394', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268497927', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268497927', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268497927', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268497927', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268497927', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268497927', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268497927', 'Tutorial', 'lvl', '6');
INSERT INTO `character_quests` VALUES ('268497927', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268497927', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268498074', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268498074', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268498074', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268498074', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268498074', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268498074', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268498074', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268498074', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268498074', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268498107', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268498107', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268498107', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268498107', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268498107', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268498107', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268498107', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268498107', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268498107', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268498118', 'NewbieHelper', '<state>', 'COMPLETED');
INSERT INTO `character_quests` VALUES ('268498118', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268498118', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268498118', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268498118', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268498118', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268498118', 'Tutorial', 'lvl', '19');
INSERT INTO `character_quests` VALUES ('268498118', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268498118', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268498467', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268498467', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268498467', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268498467', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268498467', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268498467', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268498467', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268498467', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268498467', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268503745', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268503745', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268503745', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268503745', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268503745', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268503745', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268503745', 'Tutorial', 'lvl', '19');
INSERT INTO `character_quests` VALUES ('268503745', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268503745', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268504379', 'NewbieHelper', '<state>', 'COMPLETED');
INSERT INTO `character_quests` VALUES ('268504379', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268504379', 'Q007_ATripBegins', '<state>', 'COMPLETED');
INSERT INTO `character_quests` VALUES ('268504379', 'Q161_FruitOfTheMotherTree', '<state>', 'COMPLETED');
INSERT INTO `character_quests` VALUES ('268504379', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268504379', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268504379', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268504379', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268504379', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268504379', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268504379', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268509776', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268509776', 'NewbieHelper', 'onlyone', '1');
INSERT INTO `character_quests` VALUES ('268509776', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268509776', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268509776', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268509776', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268509776', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268509776', 'Tutorial', 'ucMemo', '5');
INSERT INTO `character_quests` VALUES ('268523291', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268523291', 'NewbieHelper', 'step', '1');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', 'Die', '1');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', 'Ex', '2');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268523291', 'Tutorial', 'ucMemo', '1');
INSERT INTO `character_quests` VALUES ('268543578', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268543578', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268543578', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268543578', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268543578', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268543578', 'Tutorial', 'lvl', '35');
INSERT INTO `character_quests` VALUES ('268543578', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268543578', 'Tutorial', 'ucMemo', '3');
INSERT INTO `character_quests` VALUES ('268631868', 'NewbieHelper', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268631868', 'NewbieHelper', 'step', '2');
INSERT INTO `character_quests` VALUES ('268631868', 'Tutorial', '<state>', 'STARTED');
INSERT INTO `character_quests` VALUES ('268631868', 'Tutorial', 'Ex', '4');
INSERT INTO `character_quests` VALUES ('268631868', 'Tutorial', 'Gemstone', '1');
INSERT INTO `character_quests` VALUES ('268631868', 'Tutorial', 'HP', '1');
INSERT INTO `character_quests` VALUES ('268631868', 'Tutorial', 'lvl', '75');
INSERT INTO `character_quests` VALUES ('268631868', 'Tutorial', 'sit', '8388608');
INSERT INTO `character_quests` VALUES ('268631868', 'Tutorial', 'ucMemo', '3');

-- ----------------------------
-- Table structure for `character_raid_points`
-- ----------------------------
DROP TABLE IF EXISTS `character_raid_points`;
CREATE TABLE `character_raid_points` (
  `char_id` int(10) unsigned NOT NULL DEFAULT 0,
  `boss_id` int(10) unsigned NOT NULL DEFAULT 0,
  `points` int(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`,`boss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_raid_points
-- ----------------------------

-- ----------------------------
-- Table structure for `character_recipebook`
-- ----------------------------
DROP TABLE IF EXISTS `character_recipebook`;
CREATE TABLE `character_recipebook` (
  `charId` int(10) unsigned NOT NULL DEFAULT 0,
  `recipeId` smallint(6) NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`recipeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_recipebook
-- ----------------------------
INSERT INTO `character_recipebook` VALUES ('268498467', '67');
INSERT INTO `character_recipebook` VALUES ('268498467', '147');

-- ----------------------------
-- Table structure for `character_recommends`
-- ----------------------------
DROP TABLE IF EXISTS `character_recommends`;
CREATE TABLE `character_recommends` (
  `char_id` int(11) NOT NULL DEFAULT 0,
  `target_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`,`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_recommends
-- ----------------------------

-- ----------------------------
-- Table structure for `character_relations`
-- ----------------------------
DROP TABLE IF EXISTS `character_relations`;
CREATE TABLE `character_relations` (
  `char_id` int(10) unsigned NOT NULL DEFAULT 0,
  `friend_id` int(10) unsigned NOT NULL DEFAULT 0,
  `relation` int(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`,`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_relations
-- ----------------------------

-- ----------------------------
-- Table structure for `character_shortcuts`
-- ----------------------------
DROP TABLE IF EXISTS `character_shortcuts`;
CREATE TABLE `character_shortcuts` (
  `char_obj_id` int(10) unsigned NOT NULL DEFAULT 0,
  `slot` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `page` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `type` varchar(6) NOT NULL DEFAULT 'NONE',
  `id` int(10) unsigned NOT NULL DEFAULT 0,
  `level` smallint(6) NOT NULL DEFAULT 0,
  `class_index` tinyint(3) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`slot`,`page`,`class_index`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_shortcuts
-- ----------------------------
INSERT INTO `character_shortcuts` VALUES ('268493142', '0', '0', 'MACRO', '1001', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493142', '3', '0', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '11', '0', 'ITEM', '268493290', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493285', '11', '1', 'ITEM', '268493355', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493302', '11', '0', 'ITEM', '268493307', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '0', '0', 'SKILL', '1071', '10', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '1', '0', 'SKILL', '1235', '20', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '2', '0', 'SKILL', '1236', '15', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '3', '0', 'SKILL', '1265', '10', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '4', '0', 'SKILL', '1169', '10', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '5', '0', 'SKILL', '1056', '8', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '5', '2', 'MACRO', '105', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '9', '0', 'ITEM', '268503617', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '9', '2', 'ITEM', '268496895', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '10', '0', 'SKILL', '1015', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '10', '1', 'ITEM', '268503617', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '10', '2', 'ITEM', '268493687', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '11', '0', 'SKILL', '1323', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '11', '1', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493309', '11', '2', 'ITEM', '268494090', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493467', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493467', '1', '0', 'SKILL', '3', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268493467', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493467', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493467', '11', '0', 'ITEM', '268493472', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493467', '11', '2', 'ITEM', '268493895', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '0', '0', 'SKILL', '8', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '1', '0', 'SKILL', '6', '15', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '2', '0', 'SKILL', '1', '15', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '3', '0', 'SKILL', '5', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '4', '0', 'SKILL', '7', '6', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '5', '0', 'SKILL', '9', '12', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '9', '0', 'ITEM', '268494036', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '9', '1', 'SKILL', '78', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '10', '0', 'SKILL', '256', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '10', '1', 'SKILL', '121', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '11', '0', 'SKILL', '312', '10', '0');
INSERT INTO `character_shortcuts` VALUES ('268493578', '11', '1', 'ITEM', '268493638', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '1', '0', 'SKILL', '19', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '2', '0', 'SKILL', '101', '12', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '3', '0', 'SKILL', '56', '24', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '4', '0', 'SKILL', '99', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '9', '1', 'SKILL', '256', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '10', '1', 'SKILL', '312', '8', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '11', '0', 'ITEM', '268493611', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493605', '11', '1', 'ITEM', '268541766', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '11', '1', 'ITEM', '268503693', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493707', '11', '2', 'ITEM', '268498412', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '1', '0', 'SKILL', '1177', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '9', '0', 'SKILL', '1216', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '11', '0', 'ITEM', '268493813', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493809', '11', '2', 'ITEM', '268494126', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '1', '0', 'SKILL', '16', '21', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '2', '0', 'SKILL', '3', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '11', '0', 'ITEM', '268493883', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '11', '1', 'ITEM', '268495986', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493876', '11', '2', 'SKILL', '312', '4', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '1', '0', 'SKILL', '302', '5', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '2', '0', 'SKILL', '444', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '9', '1', 'SKILL', '1323', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '10', '1', 'ITEM', '268494180', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '11', '0', 'ITEM', '268493901', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493896', '11', '1', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '0', '0', 'SKILL', '30', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '1', '0', 'SKILL', '263', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '2', '0', 'SKILL', '16', '24', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '10', '0', 'SKILL', '1323', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '10', '1', 'SKILL', '1324', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '10', '2', 'ITEM', '268496201', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '11', '0', 'SKILL', '312', '8', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '11', '1', 'SKILL', '256', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493913', '11', '2', 'ITEM', '268496101', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '1', '0', 'SKILL', '1177', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '9', '0', 'SKILL', '1216', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '11', '0', 'ITEM', '268493988', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268493980', '11', '2', 'ITEM', '268494261', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '0', '1', 'SKILL', '94', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '0', '2', 'SKILL', '121', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '1', '0', 'SKILL', '255', '12', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '1', '1', 'SKILL', '312', '4', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494048', '11', '0', 'ITEM', '268494056', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494184', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494184', '1', '0', 'SKILL', '42', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494184', '2', '0', 'SKILL', '254', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494184', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494184', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494184', '11', '0', 'ITEM', '268494189', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494184', '11', '1', 'ITEM', '268494724', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '0', '0', 'SKILL', '1071', '14', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '0', '1', 'MACRO', '1001', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '1', '0', 'SKILL', '1235', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '2', '0', 'SKILL', '1265', '14', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '2', '1', 'SKILL', '1286', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '3', '0', 'SKILL', '1264', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '3', '1', 'SKILL', '1287', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '4', '0', 'SKILL', '1231', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '5', '0', 'SKILL', '1288', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '6', '0', 'SKILL', '1290', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '10', '0', 'SKILL', '1015', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '10', '1', 'ITEM', '268494178', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '11', '1', 'ITEM', '268495235', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494228', '11', '2', 'MACRO', '1002', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '1', '0', 'SKILL', '36', '18', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '2', '0', 'SKILL', '245', '15', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '3', '0', 'SKILL', '286', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '11', '0', 'ITEM', '268494329', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494324', '11', '1', 'ITEM', '268494448', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '0', '1', 'SKILL', '112', '4', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '1', '0', 'SKILL', '18', '23', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '2', '0', 'SKILL', '400', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '3', '0', 'SKILL', '402', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '4', '0', 'SKILL', '110', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '10', '1', 'ITEM', '268498340', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '11', '0', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494356', '11', '1', 'SKILL', '288', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '0', '0', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '1', '0', 'SKILL', '1230', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '2', '0', 'SKILL', '1083', '17', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '3', '0', 'SKILL', '1339', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '3', '2', 'ITEM', '268494082', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '10', '2', 'ITEM', '268494190', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '11', '1', 'ITEM', '268494830', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494406', '11', '2', 'ITEM', '268494123', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '0', 'SKILL', '1083', '17', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '0', 'SKILL', '8', '7', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '0', 'ACTION', '2', '-1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '1', 'SKILL', '78', '2', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '1', 'SKILL', '131', '2', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '0', '2', 'MACRO', '105', '-1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '1', '0', 'SKILL', '1230', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '1', '0', 'SKILL', '6', '37', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '1', '0', 'SKILL', '101', '15', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '1', '1', 'SKILL', '121', '1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '1', '1', 'SKILL', '99', '1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '2', '0', 'SKILL', '1296', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '2', '0', 'SKILL', '9', '34', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '2', '0', 'SKILL', '19', '12', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '2', '1', 'SKILL', '287', '3', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '2', '1', 'SKILL', '111', '1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '3', '0', 'SKILL', '1015', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '3', '0', 'SKILL', '5', '31', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '3', '0', 'SKILL', '4', '1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '4', '0', 'SKILL', '1292', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '4', '0', 'ACTION', '2', '-1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '4', '1', 'SKILL', '87', '1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '5', '0', 'SKILL', '1289', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '5', '0', 'SKILL', '1', '37', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '5', '1', 'SKILL', '75', '1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '6', '0', 'SKILL', '1288', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '6', '0', 'SKILL', '7', '28', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '8', '0', 'SKILL', '1285', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '8', '1', 'ITEM', '268493528', '-1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '8', '1', 'ITEM', '268493528', '-1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '9', '0', 'SKILL', '1286', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '9', '1', 'SKILL', '256', '1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '9', '1', 'SKILL', '256', '1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '10', '0', 'SKILL', '1287', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '10', '0', 'ITEM', '268499293', '-1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '10', '1', 'SKILL', '312', '20', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '10', '1', 'SKILL', '312', '9', '2');
INSERT INTO `character_shortcuts` VALUES ('268494667', '11', '0', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '11', '0', 'ITEM', '268493531', '-1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '11', '1', 'ITEM', '268498673', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '11', '2', 'SKILL', '337', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494667', '11', '2', 'MACRO', '1000', '-1', '1');
INSERT INTO `character_shortcuts` VALUES ('268494667', '11', '2', 'MACRO', '1000', '-1', '2');
INSERT INTO `character_shortcuts` VALUES ('268494751', '0', '0', 'SKILL', '1083', '17', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '0', '1', 'SKILL', '1287', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '0', '2', 'MACRO', '1003', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '1', '0', 'SKILL', '1230', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '1', '1', 'SKILL', '1286', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '2', '0', 'SKILL', '1239', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '2', '1', 'SKILL', '1285', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '3', '0', 'SKILL', '1234', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '4', '0', 'SKILL', '1015', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '4', '1', 'SKILL', '1288', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '5', '0', 'SKILL', '1291', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '5', '1', 'SKILL', '1294', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '6', '0', 'SKILL', '1235', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '7', '0', 'SKILL', '1267', '14', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '7', '1', 'SKILL', '1326', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '8', '0', 'ITEM', '268494880', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '8', '1', 'SKILL', '1327', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '9', '0', 'ITEM', '268505148', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '10', '0', 'SKILL', '1323', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '10', '1', 'ITEM', '268499312', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '11', '0', 'SKILL', '1169', '14', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '11', '1', 'MACRO', '1002', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268494751', '11', '2', 'ITEM', '268493935', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495502', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495632', '11', '0', 'ITEM', '268495637', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '11', '0', 'ITEM', '268495879', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268495874', '11', '2', 'ITEM', '268496168', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '0', '0', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '0', '1', 'MACRO', '1012', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '0', '2', 'MACRO', '1006', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '0', '3', 'MACRO', '1003', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '1', '0', 'MACRO', '1002', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '1', '2', 'MACRO', '1005', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '2', '0', 'MACRO', '1011', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '2', '1', 'MACRO', '1013', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '2', '2', 'MACRO', '1007', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '2', '3', 'MACRO', '1004', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '3', '2', 'MACRO', '1008', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '6', '0', 'ACTION', '4', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '6', '1', 'ITEM', '268493716', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '7', '0', 'MACRO', '1001', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '8', '0', 'MACRO', '1004', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '10', '2', 'ITEM', '268494175', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '10', '4', 'ITEM', '268493337', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '11', '0', 'MACRO', '1010', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497182', '11', '1', 'ITEM', '268493324', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497394', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497394', '10', '2', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497394', '11', '0', 'SKILL', '1323', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '11', '0', 'ITEM', '268497932', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268497927', '11', '1', 'ITEM', '268497943', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '1', '0', 'SKILL', '56', '24', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '1', '2', 'MACRO', '101', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '2', '0', 'ITEM', '268502762', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '3', '0', 'ITEM', '268502760', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '10', '1', 'SKILL', '312', '5', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '11', '0', 'SKILL', '4', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498074', '11', '1', 'ITEM', '268498304', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '0', '0', 'SKILL', '1090', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '1', '0', 'SKILL', '1001', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '2', '0', 'SKILL', '1095', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '3', '0', 'SKILL', '1097', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '10', '1', 'ITEM', '268499650', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '11', '0', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '11', '1', 'SKILL', '1001', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268498107', '11', '2', 'ITEM', '268499651', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '1', '0', 'SKILL', '3', '9', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '11', '0', 'ITEM', '268498123', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498118', '11', '1', 'ITEM', '268498332', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '1', '0', 'SKILL', '254', '4', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '2', '0', 'SKILL', '42', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '11', '0', 'SKILL', '1323', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268498467', '11', '1', 'ITEM', '268498497', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '0', '0', 'SKILL', '1015', '12', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '1', '0', 'SKILL', '1177', '5', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '2', '0', 'SKILL', '1147', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '3', '0', 'SKILL', '1184', '4', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '8', '0', 'SKILL', '1040', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '9', '0', 'SKILL', '1078', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '10', '0', 'SKILL', '1085', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '11', '0', 'SKILL', '1204', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268503745', '11', '1', 'ITEM', '268503899', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '0', '0', 'SKILL', '1071', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '1', '0', 'SKILL', '1235', '6', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '2', '0', 'SKILL', '1236', '6', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '3', '0', 'SKILL', '1265', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '4', '0', 'SKILL', '1015', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '5', '0', 'SKILL', '1169', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '6', '0', 'SKILL', '1056', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '9', '0', 'SKILL', '1323', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '10', '1', 'ITEM', '268508063', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '11', '0', 'MACRO', '1000', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268504379', '11', '2', 'ITEM', '268504974', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '1', '0', 'SKILL', '302', '3', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '2', '0', 'SKILL', '254', '5', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '3', '0', 'SKILL', '42', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '4', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '11', '0', 'ITEM', '268509781', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268509776', '11', '2', 'ITEM', '268510099', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '1', '0', 'SKILL', '254', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '2', '0', 'SKILL', '42', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '3', '0', 'SKILL', '100', '15', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '11', '0', 'ITEM', '268523296', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268523291', '11', '1', 'ITEM', '268527961', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '0', '0', 'ACTION', '2', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '1', '0', 'SKILL', '1177', '5', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '3', '0', 'ACTION', '5', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '5', '1', 'MACRO', '107', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '9', '0', 'SKILL', '1216', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '10', '0', 'ACTION', '0', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '11', '0', 'ITEM', '268543582', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268543578', '11', '1', 'ITEM', '268543894', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '0', '0', 'ITEM', '268634343', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '0', '1', 'MACRO', '102', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '0', '2', 'MACRO', '100', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '1', '0', 'SKILL', '8', '7', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '1', '1', 'MACRO', '103', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '1', '2', 'MACRO', '101', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '2', '0', 'SKILL', '6', '37', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '2', '1', 'MACRO', '104', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '3', '0', 'SKILL', '345', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '3', '1', 'MACRO', '105', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '4', '0', 'SKILL', '5', '31', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '4', '1', 'MACRO', '106', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '5', '0', 'SKILL', '9', '34', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '5', '1', 'MACRO', '107', '0', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '5', '2', 'SKILL', '75', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '6', '0', 'SKILL', '7', '28', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '6', '2', 'SKILL', '87', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '7', '0', 'SKILL', '261', '22', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '7', '2', 'SKILL', '104', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '8', '0', 'SKILL', '451', '2', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '9', '0', 'SKILL', '442', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '9', '1', 'SKILL', '312', '20', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '10', '1', 'SKILL', '256', '1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '10', '2', 'ITEM', '268631873', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '11', '1', 'ITEM', '268634167', '-1', '0');
INSERT INTO `character_shortcuts` VALUES ('268631868', '11', '2', 'ACTION', '0', '-1', '0');

-- ----------------------------
-- Table structure for `character_skills`
-- ----------------------------
DROP TABLE IF EXISTS `character_skills`;
CREATE TABLE `character_skills` (
  `char_obj_id` int(10) unsigned NOT NULL DEFAULT 0,
  `skill_id` int(11) NOT NULL DEFAULT 0,
  `skill_level` int(3) NOT NULL DEFAULT 1,
  `class_index` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_skills
-- ----------------------------
INSERT INTO `character_skills` VALUES ('268436061', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268436061', '15', '52', '0');
INSERT INTO `character_skills` VALUES ('268436061', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268436061', '21', '3', '0');
INSERT INTO `character_skills` VALUES ('268436061', '28', '12', '0');
INSERT INTO `character_skills` VALUES ('268436061', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268436061', '58', '55', '0');
INSERT INTO `character_skills` VALUES ('268436061', '61', '3', '0');
INSERT INTO `character_skills` VALUES ('268436061', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268436061', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268436061', '98', '5', '0');
INSERT INTO `character_skills` VALUES ('268436061', '102', '16', '0');
INSERT INTO `character_skills` VALUES ('268436061', '110', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '112', '2', '0');
INSERT INTO `character_skills` VALUES ('268436061', '123', '3', '0');
INSERT INTO `character_skills` VALUES ('268436061', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268436061', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268436061', '147', '51', '0');
INSERT INTO `character_skills` VALUES ('268436061', '153', '2', '0');
INSERT INTO `character_skills` VALUES ('268436061', '191', '6', '0');
INSERT INTO `character_skills` VALUES ('268436061', '196', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '217', '45', '0');
INSERT INTO `character_skills` VALUES ('268436061', '230', '2', '0');
INSERT INTO `character_skills` VALUES ('268436061', '232', '15', '0');
INSERT INTO `character_skills` VALUES ('268436061', '264', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '265', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '266', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '267', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '268', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '269', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '270', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '304', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '305', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '306', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '308', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '349', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '363', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '364', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '402', '10', '0');
INSERT INTO `character_skills` VALUES ('268436061', '407', '10', '0');
INSERT INTO `character_skills` VALUES ('268436061', '426', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '428', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '437', '1', '0');
INSERT INTO `character_skills` VALUES ('268436061', '455', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268436074', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268436074', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268436074', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268436074', '235', '41', '0');
INSERT INTO `character_skills` VALUES ('268436074', '236', '41', '0');
INSERT INTO `character_skills` VALUES ('268436074', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268436074', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '336', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '436', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1011', '18', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1012', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1013', '32', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1015', '15', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1016', '7', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1020', '27', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1027', '15', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1028', '19', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1031', '8', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1033', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1035', '4', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1040', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1043', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1044', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1050', '2', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1068', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1069', '42', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1073', '2', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1078', '6', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1087', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1164', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1184', '4', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1201', '9', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1204', '2', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1206', '19', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1217', '29', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1243', '6', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1255', '2', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1257', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1259', '4', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1273', '13', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1303', '2', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1304', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1353', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1354', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1355', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1359', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1393', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1394', '10', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1395', '10', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1397', '3', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1398', '10', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1400', '10', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1401', '10', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1428', '1', '0');
INSERT INTO `character_skills` VALUES ('268436074', '1430', '5', '0');
INSERT INTO `character_skills` VALUES ('268436087', '100', '12', '0');
INSERT INTO `character_skills` VALUES ('268436087', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268436087', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '211', '7', '0');
INSERT INTO `character_skills` VALUES ('268436087', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268436087', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268436087', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268436087', '250', '42', '0');
INSERT INTO `character_skills` VALUES ('268436087', '251', '45', '0');
INSERT INTO `character_skills` VALUES ('268436087', '252', '45', '0');
INSERT INTO `character_skills` VALUES ('268436087', '253', '43', '0');
INSERT INTO `character_skills` VALUES ('268436087', '260', '35', '0');
INSERT INTO `character_skills` VALUES ('268436087', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '337', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '436', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1001', '10', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1002', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1003', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1004', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1005', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1006', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1007', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1008', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1009', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1010', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1090', '6', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1092', '19', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1095', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1096', '16', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1097', '20', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1099', '15', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1100', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1101', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1102', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1104', '14', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1105', '18', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1107', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1108', '4', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1208', '17', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1209', '6', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1210', '4', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1213', '13', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1229', '4', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1245', '14', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1246', '12', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1247', '14', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1248', '12', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1249', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1250', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1256', '13', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1260', '3', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1261', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1282', '2', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1283', '13', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1305', '5', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1306', '6', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1364', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1365', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1366', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1367', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1414', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1415', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1416', '1', '0');
INSERT INTO `character_skills` VALUES ('268436087', '1427', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '33', '8', '0');
INSERT INTO `character_skills` VALUES ('268436100', '143', '2', '0');
INSERT INTO `character_skills` VALUES ('268436100', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268436100', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268436100', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268436100', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268436100', '234', '41', '0');
INSERT INTO `character_skills` VALUES ('268436100', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268436100', '258', '33', '0');
INSERT INTO `character_skills` VALUES ('268436100', '285', '8', '0');
INSERT INTO `character_skills` VALUES ('268436100', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '338', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '435', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1126', '34', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1127', '45', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1128', '18', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1139', '2', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1140', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1141', '2', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1146', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1147', '6', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1151', '2', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1157', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1160', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1167', '2', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1168', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1178', '8', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1206', '19', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1222', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1224', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1228', '18', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1262', '5', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1266', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1278', '14', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1281', '9', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1299', '2', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1300', '3', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1301', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1330', '8', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1333', '10', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1348', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1349', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1351', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1380', '10', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1385', '5', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1403', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1408', '1', '0');
INSERT INTO `character_skills` VALUES ('268436100', '1424', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268436113', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '211', '7', '0');
INSERT INTO `character_skills` VALUES ('268436113', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268436113', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268436113', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268436113', '235', '41', '0');
INSERT INTO `character_skills` VALUES ('268436113', '236', '41', '0');
INSERT INTO `character_skills` VALUES ('268436113', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268436113', '259', '33', '0');
INSERT INTO `character_skills` VALUES ('268436113', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '336', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '436', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1011', '18', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1012', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1015', '15', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1016', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1027', '15', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1031', '8', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1032', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1033', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1035', '4', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1036', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1040', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1043', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1044', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1045', '6', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1048', '6', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1050', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1062', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1068', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1073', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1075', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1077', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1078', '6', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1085', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1086', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1147', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1164', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1168', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1182', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1184', '4', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1189', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1191', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1201', '33', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1204', '2', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1240', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1242', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1243', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1272', '13', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1352', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1356', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1358', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1359', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1388', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1389', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1392', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1393', '3', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1395', '10', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1398', '10', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1411', '1', '0');
INSERT INTO `character_skills` VALUES ('268436113', '1412', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '100', '12', '0');
INSERT INTO `character_skills` VALUES ('268436127', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268436127', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '211', '7', '0');
INSERT INTO `character_skills` VALUES ('268436127', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268436127', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268436127', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268436127', '250', '42', '0');
INSERT INTO `character_skills` VALUES ('268436127', '251', '45', '0');
INSERT INTO `character_skills` VALUES ('268436127', '252', '45', '0');
INSERT INTO `character_skills` VALUES ('268436127', '253', '43', '0');
INSERT INTO `character_skills` VALUES ('268436127', '260', '35', '0');
INSERT INTO `character_skills` VALUES ('268436127', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '336', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '436', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1001', '10', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1002', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1003', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1005', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1006', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1007', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1009', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1010', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1090', '6', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1092', '19', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1095', '5', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1096', '2', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1097', '20', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1099', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1100', '2', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1101', '2', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1102', '6', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1105', '18', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1107', '2', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1208', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1209', '2', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1229', '18', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1244', '4', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1245', '14', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1251', '2', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1252', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1253', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1284', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1308', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1309', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1310', '4', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1362', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1363', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1390', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1391', '3', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1413', '1', '0');
INSERT INTO `character_skills` VALUES ('268436127', '1429', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '10', '8', '0');
INSERT INTO `character_skills` VALUES ('268442483', '143', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268442483', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268442483', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268442483', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268442483', '234', '41', '0');
INSERT INTO `character_skills` VALUES ('268442483', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268442483', '258', '33', '0');
INSERT INTO `character_skills` VALUES ('268442483', '285', '8', '0');
INSERT INTO `character_skills` VALUES ('268442483', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '338', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '435', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1083', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1111', '18', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1126', '34', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1127', '45', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1139', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1140', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1141', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1144', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1147', '6', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1151', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1157', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1160', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1164', '5', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1167', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1168', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1220', '8', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1222', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1225', '18', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1262', '5', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1274', '4', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1276', '14', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1279', '9', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1299', '2', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1300', '3', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1301', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1328', '8', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1331', '10', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1346', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1349', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1350', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1351', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1380', '10', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1383', '5', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1386', '10', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1403', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1406', '1', '0');
INSERT INTO `character_skills` VALUES ('268442483', '1424', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '2', '19', '0');
INSERT INTO `character_skills` VALUES ('268447460', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268447460', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268447460', '28', '12', '0');
INSERT INTO `character_skills` VALUES ('268447460', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268447460', '70', '53', '0');
INSERT INTO `character_skills` VALUES ('268447460', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268447460', '84', '3', '0');
INSERT INTO `character_skills` VALUES ('268447460', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268447460', '105', '24', '0');
INSERT INTO `character_skills` VALUES ('268447460', '110', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '112', '2', '0');
INSERT INTO `character_skills` VALUES ('268447460', '115', '17', '0');
INSERT INTO `character_skills` VALUES ('268447460', '122', '15', '0');
INSERT INTO `character_skills` VALUES ('268447460', '129', '5', '0');
INSERT INTO `character_skills` VALUES ('268447460', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268447460', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268447460', '144', '37', '0');
INSERT INTO `character_skills` VALUES ('268447460', '147', '51', '0');
INSERT INTO `character_skills` VALUES ('268447460', '153', '2', '0');
INSERT INTO `character_skills` VALUES ('268447460', '191', '6', '0');
INSERT INTO `character_skills` VALUES ('268447460', '217', '8', '0');
INSERT INTO `character_skills` VALUES ('268447460', '223', '49', '0');
INSERT INTO `character_skills` VALUES ('268447460', '232', '15', '0');
INSERT INTO `character_skills` VALUES ('268447460', '271', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '272', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '273', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '274', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '275', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '276', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '277', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '294', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '307', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '309', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '310', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '311', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '365', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '366', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '367', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '402', '10', '0');
INSERT INTO `character_skills` VALUES ('268447460', '408', '10', '0');
INSERT INTO `character_skills` VALUES ('268447460', '426', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '428', '1', '0');
INSERT INTO `character_skills` VALUES ('268447460', '455', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268462570', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268462570', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268462570', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268462570', '235', '41', '0');
INSERT INTO `character_skills` VALUES ('268462570', '236', '41', '0');
INSERT INTO `character_skills` VALUES ('268462570', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268462570', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '336', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '436', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1011', '18', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1012', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1013', '32', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1015', '15', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1016', '2', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1018', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1027', '15', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1031', '8', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1035', '4', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1040', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1059', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1068', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1073', '2', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1077', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1078', '6', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1147', '2', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1168', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1184', '4', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1189', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1201', '33', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1204', '2', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1206', '19', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1217', '29', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1219', '29', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1240', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1242', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1268', '4', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1303', '2', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1354', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1357', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1358', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1392', '3', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1395', '10', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1398', '10', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1428', '1', '0');
INSERT INTO `character_skills` VALUES ('268462570', '1430', '5', '0');
INSERT INTO `character_skills` VALUES ('268464372', '67', '7', '0');
INSERT INTO `character_skills` VALUES ('268464372', '143', '2', '0');
INSERT INTO `character_skills` VALUES ('268464372', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268464372', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268464372', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268464372', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268464372', '234', '41', '0');
INSERT INTO `character_skills` VALUES ('268464372', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268464372', '258', '33', '0');
INSERT INTO `character_skills` VALUES ('268464372', '285', '8', '0');
INSERT INTO `character_skills` VALUES ('268464372', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '338', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '435', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1126', '34', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1127', '45', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1139', '2', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1140', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1141', '2', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1145', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1164', '5', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1175', '8', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1182', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1206', '19', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1223', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1226', '18', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1227', '18', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1262', '5', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1264', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1274', '4', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1277', '14', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1280', '9', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1299', '2', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1300', '3', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1301', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1329', '9', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1332', '10', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1347', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1349', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1350', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1380', '10', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1384', '5', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1403', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1407', '1', '0');
INSERT INTO `character_skills` VALUES ('268464372', '1424', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '146', '12', '0');
INSERT INTO `character_skills` VALUES ('268476645', '164', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '212', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '213', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '228', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '229', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '235', '8', '0');
INSERT INTO `character_skills` VALUES ('268476645', '236', '8', '0');
INSERT INTO `character_skills` VALUES ('268476645', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268476645', '249', '9', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1011', '18', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1012', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1015', '15', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1016', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1027', '15', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1031', '8', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1035', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1040', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1043', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1044', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1062', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1068', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1073', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1075', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1077', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1085', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1147', '2', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1164', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1168', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1184', '4', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1191', '1', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1201', '9', '0');
INSERT INTO `character_skills` VALUES ('268476645', '1204', '2', '0');
INSERT INTO `character_skills` VALUES ('268493142', '239', '1', '0');
INSERT INTO `character_skills` VALUES ('268493142', '1320', '3', '0');
INSERT INTO `character_skills` VALUES ('268493285', '2', '19', '0');
INSERT INTO `character_skills` VALUES ('268493285', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493285', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268493285', '18', '37', '0');
INSERT INTO `character_skills` VALUES ('268493285', '22', '7', '0');
INSERT INTO `character_skills` VALUES ('268493285', '28', '49', '0');
INSERT INTO `character_skills` VALUES ('268493285', '33', '8', '0');
INSERT INTO `character_skills` VALUES ('268493285', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268493285', '70', '53', '0');
INSERT INTO `character_skills` VALUES ('268493285', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268493285', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268493285', '103', '4', '0');
INSERT INTO `character_skills` VALUES ('268493285', '105', '24', '0');
INSERT INTO `character_skills` VALUES ('268493285', '110', '2', '0');
INSERT INTO `character_skills` VALUES ('268493285', '112', '4', '0');
INSERT INTO `character_skills` VALUES ('268493285', '115', '17', '0');
INSERT INTO `character_skills` VALUES ('268493285', '122', '15', '0');
INSERT INTO `character_skills` VALUES ('268493285', '129', '5', '0');
INSERT INTO `character_skills` VALUES ('268493285', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493285', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493285', '143', '2', '0');
INSERT INTO `character_skills` VALUES ('268493285', '147', '51', '0');
INSERT INTO `character_skills` VALUES ('268493285', '153', '4', '0');
INSERT INTO `character_skills` VALUES ('268493285', '191', '6', '0');
INSERT INTO `character_skills` VALUES ('268493285', '217', '45', '0');
INSERT INTO `character_skills` VALUES ('268493285', '223', '49', '0');
INSERT INTO `character_skills` VALUES ('268493285', '232', '52', '0');
INSERT INTO `character_skills` VALUES ('268493285', '278', '6', '0');
INSERT INTO `character_skills` VALUES ('268493285', '279', '5', '0');
INSERT INTO `character_skills` VALUES ('268493285', '288', '4', '0');
INSERT INTO `character_skills` VALUES ('268493285', '289', '15', '0');
INSERT INTO `character_skills` VALUES ('268493285', '291', '11', '0');
INSERT INTO `character_skills` VALUES ('268493285', '294', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '316', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '322', '6', '0');
INSERT INTO `character_skills` VALUES ('268493285', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '335', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '342', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '351', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '352', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '368', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '401', '10', '0');
INSERT INTO `character_skills` VALUES ('268493285', '402', '10', '0');
INSERT INTO `character_skills` VALUES ('268493285', '426', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '429', '1', '0');
INSERT INTO `character_skills` VALUES ('268493285', '450', '10', '0');
INSERT INTO `character_skills` VALUES ('268493285', '454', '1', '0');
INSERT INTO `character_skills` VALUES ('268493302', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493302', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268493302', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268493302', '58', '3', '0');
INSERT INTO `character_skills` VALUES ('268493302', '77', '1', '0');
INSERT INTO `character_skills` VALUES ('268493302', '91', '1', '0');
INSERT INTO `character_skills` VALUES ('268493302', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493302', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493309', '146', '37', '0');
INSERT INTO `character_skills` VALUES ('268493309', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '212', '5', '0');
INSERT INTO `character_skills` VALUES ('268493309', '213', '7', '0');
INSERT INTO `character_skills` VALUES ('268493309', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '229', '5', '0');
INSERT INTO `character_skills` VALUES ('268493309', '234', '33', '0');
INSERT INTO `character_skills` VALUES ('268493309', '239', '2', '0');
INSERT INTO `character_skills` VALUES ('268493309', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '249', '34', '0');
INSERT INTO `character_skills` VALUES ('268493309', '285', '23', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1047', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1056', '8', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1069', '34', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1071', '10', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1072', '4', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1126', '6', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1127', '12', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1145', '1', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1164', '15', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1169', '10', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1174', '14', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1175', '8', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1182', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1183', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1206', '5', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1223', '11', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1226', '4', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1227', '4', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1231', '20', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1235', '20', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1236', '15', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1237', '13', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1238', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1264', '3', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1265', '10', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1274', '4', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1275', '10', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1286', '1', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1295', '5', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1297', '4', '0');
INSERT INTO `character_skills` VALUES ('268493309', '1417', '3', '0');
INSERT INTO `character_skills` VALUES ('268493467', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493467', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268493467', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268493467', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493467', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493467', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '1', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493578', '5', '9', '0');
INSERT INTO `character_skills` VALUES ('268493578', '6', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '7', '6', '0');
INSERT INTO `character_skills` VALUES ('268493578', '8', '3', '0');
INSERT INTO `character_skills` VALUES ('268493578', '9', '12', '0');
INSERT INTO `character_skills` VALUES ('268493578', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268493578', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268493578', '75', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '78', '2', '0');
INSERT INTO `character_skills` VALUES ('268493578', '80', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '87', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '100', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '104', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '121', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493578', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493578', '144', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '148', '2', '0');
INSERT INTO `character_skills` VALUES ('268493578', '190', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '211', '3', '0');
INSERT INTO `character_skills` VALUES ('268493578', '212', '5', '0');
INSERT INTO `character_skills` VALUES ('268493578', '216', '8', '0');
INSERT INTO `character_skills` VALUES ('268493578', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '227', '28', '0');
INSERT INTO `character_skills` VALUES ('268493578', '231', '28', '0');
INSERT INTO `character_skills` VALUES ('268493578', '245', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '255', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268493578', '257', '23', '0');
INSERT INTO `character_skills` VALUES ('268493578', '260', '15', '0');
INSERT INTO `character_skills` VALUES ('268493578', '287', '2', '0');
INSERT INTO `character_skills` VALUES ('268493578', '290', '4', '0');
INSERT INTO `character_skills` VALUES ('268493578', '312', '10', '0');
INSERT INTO `character_skills` VALUES ('268493605', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493605', '15', '24', '0');
INSERT INTO `character_skills` VALUES ('268493605', '16', '24', '0');
INSERT INTO `character_skills` VALUES ('268493605', '19', '9', '0');
INSERT INTO `character_skills` VALUES ('268493605', '21', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '24', '3', '0');
INSERT INTO `character_skills` VALUES ('268493605', '27', '5', '0');
INSERT INTO `character_skills` VALUES ('268493605', '56', '24', '0');
INSERT INTO `character_skills` VALUES ('268493605', '58', '27', '0');
INSERT INTO `character_skills` VALUES ('268493605', '61', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '96', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '99', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '101', '12', '0');
INSERT INTO `character_skills` VALUES ('268493605', '102', '4', '0');
INSERT INTO `character_skills` VALUES ('268493605', '111', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '113', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '123', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '137', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493605', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493605', '169', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '171', '3', '0');
INSERT INTO `character_skills` VALUES ('268493605', '173', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '195', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '198', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '208', '24', '0');
INSERT INTO `character_skills` VALUES ('268493605', '209', '8', '0');
INSERT INTO `character_skills` VALUES ('268493605', '225', '2', '0');
INSERT INTO `character_skills` VALUES ('268493605', '230', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '233', '19', '0');
INSERT INTO `character_skills` VALUES ('268493605', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '303', '1', '0');
INSERT INTO `character_skills` VALUES ('268493605', '312', '8', '0');
INSERT INTO `character_skills` VALUES ('268493707', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493707', '15', '12', '0');
INSERT INTO `character_skills` VALUES ('268493707', '16', '21', '0');
INSERT INTO `character_skills` VALUES ('268493707', '21', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '27', '4', '0');
INSERT INTO `character_skills` VALUES ('268493707', '56', '21', '0');
INSERT INTO `character_skills` VALUES ('268493707', '58', '15', '0');
INSERT INTO `character_skills` VALUES ('268493707', '61', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268493707', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268493707', '96', '2', '0');
INSERT INTO `character_skills` VALUES ('268493707', '99', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '111', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '113', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '137', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493707', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493707', '169', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '173', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '195', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '198', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '208', '12', '0');
INSERT INTO `character_skills` VALUES ('268493707', '209', '6', '0');
INSERT INTO `character_skills` VALUES ('268493707', '225', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '230', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '233', '8', '0');
INSERT INTO `character_skills` VALUES ('268493707', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268493707', '312', '4', '0');
INSERT INTO `character_skills` VALUES ('268493809', '146', '2', '0');
INSERT INTO `character_skills` VALUES ('268493809', '244', '1', '0');
INSERT INTO `character_skills` VALUES ('268493809', '249', '1', '0');
INSERT INTO `character_skills` VALUES ('268493809', '1011', '3', '0');
INSERT INTO `character_skills` VALUES ('268493809', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268493809', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268493809', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268493809', '1168', '1', '0');
INSERT INTO `character_skills` VALUES ('268493809', '1177', '3', '0');
INSERT INTO `character_skills` VALUES ('268493809', '1184', '2', '0');
INSERT INTO `character_skills` VALUES ('268493876', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493876', '15', '12', '0');
INSERT INTO `character_skills` VALUES ('268493876', '16', '21', '0');
INSERT INTO `character_skills` VALUES ('268493876', '21', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '27', '4', '0');
INSERT INTO `character_skills` VALUES ('268493876', '56', '21', '0');
INSERT INTO `character_skills` VALUES ('268493876', '58', '15', '0');
INSERT INTO `character_skills` VALUES ('268493876', '61', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268493876', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268493876', '96', '2', '0');
INSERT INTO `character_skills` VALUES ('268493876', '99', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '111', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '113', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '137', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493876', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493876', '169', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '173', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '195', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '198', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '208', '12', '0');
INSERT INTO `character_skills` VALUES ('268493876', '209', '6', '0');
INSERT INTO `character_skills` VALUES ('268493876', '225', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '230', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '233', '8', '0');
INSERT INTO `character_skills` VALUES ('268493876', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268493876', '312', '4', '0');
INSERT INTO `character_skills` VALUES ('268493896', '34', '2', '0');
INSERT INTO `character_skills` VALUES ('268493896', '36', '21', '0');
INSERT INTO `character_skills` VALUES ('268493896', '42', '1', '0');
INSERT INTO `character_skills` VALUES ('268493896', '60', '1', '0');
INSERT INTO `character_skills` VALUES ('268493896', '100', '15', '0');
INSERT INTO `character_skills` VALUES ('268493896', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493896', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493896', '148', '6', '0');
INSERT INTO `character_skills` VALUES ('268493896', '150', '3', '0');
INSERT INTO `character_skills` VALUES ('268493896', '172', '1', '0');
INSERT INTO `character_skills` VALUES ('268493896', '205', '29', '0');
INSERT INTO `character_skills` VALUES ('268493896', '211', '6', '0');
INSERT INTO `character_skills` VALUES ('268493896', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268493896', '216', '29', '0');
INSERT INTO `character_skills` VALUES ('268493896', '227', '34', '0');
INSERT INTO `character_skills` VALUES ('268493896', '231', '34', '0');
INSERT INTO `character_skills` VALUES ('268493896', '245', '15', '0');
INSERT INTO `character_skills` VALUES ('268493896', '254', '7', '0');
INSERT INTO `character_skills` VALUES ('268493896', '260', '21', '0');
INSERT INTO `character_skills` VALUES ('268493896', '302', '5', '0');
INSERT INTO `character_skills` VALUES ('268493896', '422', '1', '0');
INSERT INTO `character_skills` VALUES ('268493896', '424', '1', '0');
INSERT INTO `character_skills` VALUES ('268493896', '444', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '2', '7', '0');
INSERT INTO `character_skills` VALUES ('268493913', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268493913', '16', '24', '0');
INSERT INTO `character_skills` VALUES ('268493913', '27', '8', '0');
INSERT INTO `character_skills` VALUES ('268493913', '30', '9', '0');
INSERT INTO `character_skills` VALUES ('268493913', '56', '24', '0');
INSERT INTO `character_skills` VALUES ('268493913', '70', '25', '0');
INSERT INTO `character_skills` VALUES ('268493913', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268493913', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268493913', '96', '2', '0');
INSERT INTO `character_skills` VALUES ('268493913', '99', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '101', '3', '0');
INSERT INTO `character_skills` VALUES ('268493913', '105', '8', '0');
INSERT INTO `character_skills` VALUES ('268493913', '106', '2', '0');
INSERT INTO `character_skills` VALUES ('268493913', '111', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '113', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '115', '5', '0');
INSERT INTO `character_skills` VALUES ('268493913', '122', '3', '0');
INSERT INTO `character_skills` VALUES ('268493913', '129', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268493913', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268493913', '169', '2', '0');
INSERT INTO `character_skills` VALUES ('268493913', '171', '3', '0');
INSERT INTO `character_skills` VALUES ('268493913', '173', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '193', '3', '0');
INSERT INTO `character_skills` VALUES ('268493913', '195', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '198', '2', '0');
INSERT INTO `character_skills` VALUES ('268493913', '208', '15', '0');
INSERT INTO `character_skills` VALUES ('268493913', '209', '17', '0');
INSERT INTO `character_skills` VALUES ('268493913', '221', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '223', '21', '0');
INSERT INTO `character_skills` VALUES ('268493913', '225', '2', '0');
INSERT INTO `character_skills` VALUES ('268493913', '233', '19', '0');
INSERT INTO `character_skills` VALUES ('268493913', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '263', '9', '0');
INSERT INTO `character_skills` VALUES ('268493913', '294', '1', '0');
INSERT INTO `character_skills` VALUES ('268493913', '312', '8', '0');
INSERT INTO `character_skills` VALUES ('268493913', '419', '1', '0');
INSERT INTO `character_skills` VALUES ('268493980', '146', '2', '0');
INSERT INTO `character_skills` VALUES ('268493980', '244', '1', '0');
INSERT INTO `character_skills` VALUES ('268493980', '249', '1', '0');
INSERT INTO `character_skills` VALUES ('268493980', '1011', '3', '0');
INSERT INTO `character_skills` VALUES ('268493980', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268493980', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268493980', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268493980', '1168', '1', '0');
INSERT INTO `character_skills` VALUES ('268493980', '1177', '3', '0');
INSERT INTO `character_skills` VALUES ('268493980', '1184', '2', '0');
INSERT INTO `character_skills` VALUES ('268494048', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268494048', '29', '9', '0');
INSERT INTO `character_skills` VALUES ('268494048', '34', '1', '0');
INSERT INTO `character_skills` VALUES ('268494048', '94', '1', '0');
INSERT INTO `character_skills` VALUES ('268494048', '100', '12', '0');
INSERT INTO `character_skills` VALUES ('268494048', '121', '1', '0');
INSERT INTO `character_skills` VALUES ('268494048', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268494048', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268494048', '148', '2', '0');
INSERT INTO `character_skills` VALUES ('268494048', '176', '1', '0');
INSERT INTO `character_skills` VALUES ('268494048', '211', '2', '0');
INSERT INTO `character_skills` VALUES ('268494048', '212', '2', '0');
INSERT INTO `character_skills` VALUES ('268494048', '216', '6', '0');
INSERT INTO `character_skills` VALUES ('268494048', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268494048', '227', '10', '0');
INSERT INTO `character_skills` VALUES ('268494048', '231', '10', '0');
INSERT INTO `character_skills` VALUES ('268494048', '245', '12', '0');
INSERT INTO `character_skills` VALUES ('268494048', '255', '12', '0');
INSERT INTO `character_skills` VALUES ('268494048', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268494048', '257', '6', '0');
INSERT INTO `character_skills` VALUES ('268494048', '293', '4', '0');
INSERT INTO `character_skills` VALUES ('268494048', '295', '1', '0');
INSERT INTO `character_skills` VALUES ('268494048', '312', '4', '0');
INSERT INTO `character_skills` VALUES ('268494184', '42', '1', '0');
INSERT INTO `character_skills` VALUES ('268494184', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268494184', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268494184', '150', '1', '0');
INSERT INTO `character_skills` VALUES ('268494184', '172', '1', '0');
INSERT INTO `character_skills` VALUES ('268494184', '254', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268494228', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268494228', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268494228', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268494228', '234', '41', '0');
INSERT INTO `character_skills` VALUES ('268494228', '239', '2', '0');
INSERT INTO `character_skills` VALUES ('268494228', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268494228', '285', '27', '0');
INSERT INTO `character_skills` VALUES ('268494228', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '337', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '370', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '371', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '372', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '373', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '433', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1047', '4', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1056', '12', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1069', '42', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1071', '14', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1072', '5', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1126', '6', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1127', '12', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1145', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1164', '19', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1169', '14', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1174', '22', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1175', '8', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1182', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1183', '4', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1206', '5', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1223', '15', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1226', '4', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1227', '4', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1231', '28', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1235', '28', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1236', '19', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1237', '17', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1238', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1264', '3', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1265', '14', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1274', '4', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1275', '14', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1286', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1287', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1288', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1290', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1293', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1295', '9', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1297', '6', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1320', '6', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1338', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1340', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1342', '1', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1417', '5', '0');
INSERT INTO `character_skills` VALUES ('268494228', '1421', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268494324', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268494324', '36', '18', '0');
INSERT INTO `character_skills` VALUES ('268494324', '48', '18', '0');
INSERT INTO `character_skills` VALUES ('268494324', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268494324', '75', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '78', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '80', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '87', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '100', '15', '0');
INSERT INTO `character_skills` VALUES ('268494324', '104', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '116', '5', '0');
INSERT INTO `character_skills` VALUES ('268494324', '121', '3', '0');
INSERT INTO `character_skills` VALUES ('268494324', '130', '2', '0');
INSERT INTO `character_skills` VALUES ('268494324', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268494324', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268494324', '148', '2', '0');
INSERT INTO `character_skills` VALUES ('268494324', '181', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '211', '6', '0');
INSERT INTO `character_skills` VALUES ('268494324', '212', '5', '0');
INSERT INTO `character_skills` VALUES ('268494324', '216', '26', '0');
INSERT INTO `character_skills` VALUES ('268494324', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '227', '31', '0');
INSERT INTO `character_skills` VALUES ('268494324', '231', '31', '0');
INSERT INTO `character_skills` VALUES ('268494324', '245', '15', '0');
INSERT INTO `character_skills` VALUES ('268494324', '255', '15', '0');
INSERT INTO `character_skills` VALUES ('268494324', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268494324', '257', '8', '0');
INSERT INTO `character_skills` VALUES ('268494324', '286', '2', '0');
INSERT INTO `character_skills` VALUES ('268494324', '287', '2', '0');
INSERT INTO `character_skills` VALUES ('268494324', '290', '5', '0');
INSERT INTO `character_skills` VALUES ('268494324', '312', '11', '0');
INSERT INTO `character_skills` VALUES ('268494324', '317', '2', '0');
INSERT INTO `character_skills` VALUES ('268494324', '452', '1', '0');
INSERT INTO `character_skills` VALUES ('268494356', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268494356', '10', '4', '0');
INSERT INTO `character_skills` VALUES ('268494356', '15', '38', '0');
INSERT INTO `character_skills` VALUES ('268494356', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268494356', '18', '23', '0');
INSERT INTO `character_skills` VALUES ('268494356', '21', '3', '0');
INSERT INTO `character_skills` VALUES ('268494356', '28', '35', '0');
INSERT INTO `character_skills` VALUES ('268494356', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268494356', '58', '41', '0');
INSERT INTO `character_skills` VALUES ('268494356', '61', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '67', '4', '0');
INSERT INTO `character_skills` VALUES ('268494356', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '102', '9', '0');
INSERT INTO `character_skills` VALUES ('268494356', '107', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '110', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '112', '4', '0');
INSERT INTO `character_skills` VALUES ('268494356', '123', '3', '0');
INSERT INTO `character_skills` VALUES ('268494356', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268494356', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268494356', '143', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '147', '37', '0');
INSERT INTO `character_skills` VALUES ('268494356', '153', '4', '0');
INSERT INTO `character_skills` VALUES ('268494356', '191', '4', '0');
INSERT INTO `character_skills` VALUES ('268494356', '197', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '217', '31', '0');
INSERT INTO `character_skills` VALUES ('268494356', '230', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '232', '38', '0');
INSERT INTO `character_skills` VALUES ('268494356', '239', '3', '0');
INSERT INTO `character_skills` VALUES ('268494356', '288', '2', '0');
INSERT INTO `character_skills` VALUES ('268494356', '291', '4', '0');
INSERT INTO `character_skills` VALUES ('268494356', '316', '1', '0');
INSERT INTO `character_skills` VALUES ('268494356', '400', '3', '0');
INSERT INTO `character_skills` VALUES ('268494356', '402', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268494406', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268494406', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268494406', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268494406', '234', '41', '0');
INSERT INTO `character_skills` VALUES ('268494406', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268494406', '285', '27', '0');
INSERT INTO `character_skills` VALUES ('268494406', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '337', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '433', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1056', '12', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1069', '42', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1072', '5', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1074', '14', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1078', '6', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1083', '17', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1111', '4', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1126', '6', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1127', '12', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1144', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1147', '6', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1151', '2', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1157', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1160', '15', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1164', '5', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1167', '2', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1168', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1169', '14', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1171', '19', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1220', '8', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1222', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1225', '4', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1230', '28', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1231', '28', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1232', '3', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1233', '4', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1274', '4', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1275', '14', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1285', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1288', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1289', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1292', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1296', '9', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1297', '6', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1338', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1339', '1', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1417', '5', '0');
INSERT INTO `character_skills` VALUES ('268494406', '1419', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1', '37', '1');
INSERT INTO `character_skills` VALUES ('268494667', '3', '9', '1');
INSERT INTO `character_skills` VALUES ('268494667', '3', '9', '2');
INSERT INTO `character_skills` VALUES ('268494667', '4', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '5', '31', '1');
INSERT INTO `character_skills` VALUES ('268494667', '6', '37', '1');
INSERT INTO `character_skills` VALUES ('268494667', '7', '28', '1');
INSERT INTO `character_skills` VALUES ('268494667', '8', '7', '1');
INSERT INTO `character_skills` VALUES ('268494667', '9', '34', '1');
INSERT INTO `character_skills` VALUES ('268494667', '16', '9', '1');
INSERT INTO `character_skills` VALUES ('268494667', '16', '24', '2');
INSERT INTO `character_skills` VALUES ('268494667', '19', '12', '2');
INSERT INTO `character_skills` VALUES ('268494667', '24', '6', '2');
INSERT INTO `character_skills` VALUES ('268494667', '27', '5', '2');
INSERT INTO `character_skills` VALUES ('268494667', '56', '9', '1');
INSERT INTO `character_skills` VALUES ('268494667', '56', '24', '2');
INSERT INTO `character_skills` VALUES ('268494667', '75', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '78', '2', '1');
INSERT INTO `character_skills` VALUES ('268494667', '80', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '87', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '88', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '96', '2', '2');
INSERT INTO `character_skills` VALUES ('268494667', '99', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '100', '15', '1');
INSERT INTO `character_skills` VALUES ('268494667', '101', '15', '2');
INSERT INTO `character_skills` VALUES ('268494667', '104', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '111', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '113', '2', '2');
INSERT INTO `character_skills` VALUES ('268494667', '121', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '131', '2', '2');
INSERT INTO `character_skills` VALUES ('268494667', '137', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '141', '3', '1');
INSERT INTO `character_skills` VALUES ('268494667', '141', '3', '2');
INSERT INTO `character_skills` VALUES ('268494667', '142', '5', '1');
INSERT INTO `character_skills` VALUES ('268494667', '142', '5', '2');
INSERT INTO `character_skills` VALUES ('268494667', '143', '2', '0');
INSERT INTO `character_skills` VALUES ('268494667', '144', '37', '1');
INSERT INTO `character_skills` VALUES ('268494667', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268494667', '148', '2', '1');
INSERT INTO `character_skills` VALUES ('268494667', '148', '4', '2');
INSERT INTO `character_skills` VALUES ('268494667', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '168', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '169', '2', '2');
INSERT INTO `character_skills` VALUES ('268494667', '171', '4', '2');
INSERT INTO `character_skills` VALUES ('268494667', '173', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '190', '37', '1');
INSERT INTO `character_skills` VALUES ('268494667', '193', '2', '2');
INSERT INTO `character_skills` VALUES ('268494667', '194', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '194', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '195', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '198', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '208', '27', '2');
INSERT INTO `character_skills` VALUES ('268494667', '209', '8', '2');
INSERT INTO `character_skills` VALUES ('268494667', '211', '3', '1');
INSERT INTO `character_skills` VALUES ('268494667', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268494667', '212', '8', '1');
INSERT INTO `character_skills` VALUES ('268494667', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268494667', '216', '8', '1');
INSERT INTO `character_skills` VALUES ('268494667', '225', '2', '2');
INSERT INTO `character_skills` VALUES ('268494667', '226', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '226', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '227', '50', '1');
INSERT INTO `character_skills` VALUES ('268494667', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268494667', '231', '50', '1');
INSERT INTO `character_skills` VALUES ('268494667', '233', '22', '2');
INSERT INTO `character_skills` VALUES ('268494667', '234', '41', '0');
INSERT INTO `character_skills` VALUES ('268494667', '239', '2', '1');
INSERT INTO `character_skills` VALUES ('268494667', '239', '2', '2');
INSERT INTO `character_skills` VALUES ('268494667', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '245', '15', '1');
INSERT INTO `character_skills` VALUES ('268494667', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268494667', '255', '15', '1');
INSERT INTO `character_skills` VALUES ('268494667', '256', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '256', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '257', '45', '1');
INSERT INTO `character_skills` VALUES ('268494667', '260', '37', '1');
INSERT INTO `character_skills` VALUES ('268494667', '261', '22', '1');
INSERT INTO `character_skills` VALUES ('268494667', '285', '27', '0');
INSERT INTO `character_skills` VALUES ('268494667', '287', '3', '1');
INSERT INTO `character_skills` VALUES ('268494667', '290', '14', '1');
INSERT INTO `character_skills` VALUES ('268494667', '297', '2', '1');
INSERT INTO `character_skills` VALUES ('268494667', '303', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '312', '20', '1');
INSERT INTO `character_skills` VALUES ('268494667', '312', '9', '2');
INSERT INTO `character_skills` VALUES ('268494667', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '331', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '337', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '424', '3', '1');
INSERT INTO `character_skills` VALUES ('268494667', '427', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '433', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '451', '2', '1');
INSERT INTO `character_skills` VALUES ('268494667', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1056', '12', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1069', '42', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1072', '5', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1074', '14', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1078', '6', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1083', '17', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1111', '4', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1126', '6', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1127', '12', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1144', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1147', '6', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1151', '2', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1157', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1160', '15', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1164', '5', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1167', '2', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1168', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1169', '14', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1171', '19', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1220', '8', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1222', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1225', '4', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1230', '28', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1231', '28', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1232', '3', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1233', '4', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1274', '4', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1275', '14', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1285', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1286', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1287', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1288', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1289', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1292', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1296', '9', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1297', '6', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1320', '4', '1');
INSERT INTO `character_skills` VALUES ('268494667', '1320', '4', '2');
INSERT INTO `character_skills` VALUES ('268494667', '1322', '1', '1');
INSERT INTO `character_skills` VALUES ('268494667', '1322', '1', '2');
INSERT INTO `character_skills` VALUES ('268494667', '1338', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1339', '1', '0');
INSERT INTO `character_skills` VALUES ('268494667', '1417', '5', '0');
INSERT INTO `character_skills` VALUES ('268494751', '143', '2', '0');
INSERT INTO `character_skills` VALUES ('268494751', '146', '45', '0');
INSERT INTO `character_skills` VALUES ('268494751', '147', '28', '0');
INSERT INTO `character_skills` VALUES ('268494751', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '212', '6', '0');
INSERT INTO `character_skills` VALUES ('268494751', '213', '8', '0');
INSERT INTO `character_skills` VALUES ('268494751', '228', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '229', '7', '0');
INSERT INTO `character_skills` VALUES ('268494751', '234', '41', '0');
INSERT INTO `character_skills` VALUES ('268494751', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '249', '42', '0');
INSERT INTO `character_skills` VALUES ('268494751', '285', '27', '0');
INSERT INTO `character_skills` VALUES ('268494751', '316', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '370', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '371', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '372', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '373', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '374', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '375', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '376', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '377', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '378', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '379', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '380', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '381', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '382', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '383', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '384', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '385', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '386', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '387', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '388', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '389', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '390', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '391', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1064', '14', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1069', '42', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1074', '14', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1083', '17', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1126', '6', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1127', '12', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1128', '4', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1146', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1147', '6', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1148', '13', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1151', '16', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1157', '5', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1159', '22', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1160', '15', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1167', '6', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1168', '7', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1169', '14', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1176', '15', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1178', '8', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1206', '5', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1222', '15', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1224', '17', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1228', '4', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1230', '28', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1234', '28', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1235', '28', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1239', '28', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1266', '3', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1267', '14', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1285', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1286', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1287', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1288', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1291', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1294', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1296', '9', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1297', '6', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1320', '1', '0');
INSERT INTO `character_skills` VALUES ('268494751', '1417', '5', '0');
INSERT INTO `character_skills` VALUES ('268495502', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268495502', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268495502', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268495502', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268495502', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268495502', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268495632', '3', '6', '0');
INSERT INTO `character_skills` VALUES ('268495632', '16', '6', '0');
INSERT INTO `character_skills` VALUES ('268495632', '56', '6', '0');
INSERT INTO `character_skills` VALUES ('268495632', '141', '2', '0');
INSERT INTO `character_skills` VALUES ('268495632', '142', '3', '0');
INSERT INTO `character_skills` VALUES ('268495632', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268495874', '42', '1', '0');
INSERT INTO `character_skills` VALUES ('268495874', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268495874', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268495874', '150', '1', '0');
INSERT INTO `character_skills` VALUES ('268495874', '172', '1', '0');
INSERT INTO `character_skills` VALUES ('268495874', '254', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '67', '7', '0');
INSERT INTO `character_skills` VALUES ('268497182', '146', '12', '0');
INSERT INTO `character_skills` VALUES ('268497182', '164', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '212', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '213', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '228', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '229', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '235', '8', '0');
INSERT INTO `character_skills` VALUES ('268497182', '236', '8', '0');
INSERT INTO `character_skills` VALUES ('268497182', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268497182', '249', '9', '0');
INSERT INTO `character_skills` VALUES ('268497182', '264', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '267', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '268', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '269', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '271', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '272', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '274', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '275', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '304', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '305', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '310', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1011', '18', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1012', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1015', '15', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1016', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1027', '15', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1031', '8', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1035', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1036', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1040', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1043', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1044', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1045', '6', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1062', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1068', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1073', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1075', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1077', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1085', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1086', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1147', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1164', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1168', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1184', '4', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1191', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1201', '9', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1204', '2', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1240', '3', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1259', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1284', '3', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1310', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1323', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1363', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1364', '1', '0');
INSERT INTO `character_skills` VALUES ('268497182', '1388', '3', '0');
INSERT INTO `character_skills` VALUES ('268497394', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268497394', '29', '9', '0');
INSERT INTO `character_skills` VALUES ('268497394', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268497394', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268497394', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268497394', '295', '1', '0');
INSERT INTO `character_skills` VALUES ('268497394', '1320', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '2', '19', '0');
INSERT INTO `character_skills` VALUES ('268497927', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268497927', '11', '12', '0');
INSERT INTO `character_skills` VALUES ('268497927', '16', '24', '0');
INSERT INTO `character_skills` VALUES ('268497927', '27', '14', '0');
INSERT INTO `character_skills` VALUES ('268497927', '30', '37', '0');
INSERT INTO `character_skills` VALUES ('268497927', '56', '24', '0');
INSERT INTO `character_skills` VALUES ('268497927', '70', '53', '0');
INSERT INTO `character_skills` VALUES ('268497927', '77', '2', '0');
INSERT INTO `character_skills` VALUES ('268497927', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268497927', '96', '6', '0');
INSERT INTO `character_skills` VALUES ('268497927', '99', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '101', '3', '0');
INSERT INTO `character_skills` VALUES ('268497927', '105', '24', '0');
INSERT INTO `character_skills` VALUES ('268497927', '106', '14', '0');
INSERT INTO `character_skills` VALUES ('268497927', '111', '2', '0');
INSERT INTO `character_skills` VALUES ('268497927', '113', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '115', '17', '0');
INSERT INTO `character_skills` VALUES ('268497927', '122', '15', '0');
INSERT INTO `character_skills` VALUES ('268497927', '129', '5', '0');
INSERT INTO `character_skills` VALUES ('268497927', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268497927', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268497927', '169', '2', '0');
INSERT INTO `character_skills` VALUES ('268497927', '171', '8', '0');
INSERT INTO `character_skills` VALUES ('268497927', '173', '2', '0');
INSERT INTO `character_skills` VALUES ('268497927', '193', '6', '0');
INSERT INTO `character_skills` VALUES ('268497927', '195', '2', '0');
INSERT INTO `character_skills` VALUES ('268497927', '198', '3', '0');
INSERT INTO `character_skills` VALUES ('268497927', '208', '15', '0');
INSERT INTO `character_skills` VALUES ('268497927', '209', '45', '0');
INSERT INTO `character_skills` VALUES ('268497927', '221', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '223', '49', '0');
INSERT INTO `character_skills` VALUES ('268497927', '225', '3', '0');
INSERT INTO `character_skills` VALUES ('268497927', '233', '47', '0');
INSERT INTO `character_skills` VALUES ('268497927', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '263', '37', '0');
INSERT INTO `character_skills` VALUES ('268497927', '294', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '312', '20', '0');
INSERT INTO `character_skills` VALUES ('268497927', '321', '10', '0');
INSERT INTO `character_skills` VALUES ('268497927', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '330', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '334', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '344', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '355', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '357', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '358', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '410', '3', '0');
INSERT INTO `character_skills` VALUES ('268497927', '412', '10', '0');
INSERT INTO `character_skills` VALUES ('268497927', '419', '4', '0');
INSERT INTO `character_skills` VALUES ('268497927', '426', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '432', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '447', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '453', '1', '0');
INSERT INTO `character_skills` VALUES ('268497927', '460', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268498074', '4', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '16', '24', '0');
INSERT INTO `character_skills` VALUES ('268498074', '27', '5', '0');
INSERT INTO `character_skills` VALUES ('268498074', '56', '24', '0');
INSERT INTO `character_skills` VALUES ('268498074', '96', '2', '0');
INSERT INTO `character_skills` VALUES ('268498074', '99', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '101', '3', '0');
INSERT INTO `character_skills` VALUES ('268498074', '111', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '113', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '137', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268498074', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268498074', '148', '2', '0');
INSERT INTO `character_skills` VALUES ('268498074', '168', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '169', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '171', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '173', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '193', '2', '0');
INSERT INTO `character_skills` VALUES ('268498074', '195', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '198', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '208', '15', '0');
INSERT INTO `character_skills` VALUES ('268498074', '209', '8', '0');
INSERT INTO `character_skills` VALUES ('268498074', '225', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '233', '10', '0');
INSERT INTO `character_skills` VALUES ('268498074', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268498074', '312', '5', '0');
INSERT INTO `character_skills` VALUES ('268498107', '146', '4', '0');
INSERT INTO `character_skills` VALUES ('268498107', '250', '2', '0');
INSERT INTO `character_skills` VALUES ('268498107', '251', '4', '0');
INSERT INTO `character_skills` VALUES ('268498107', '252', '4', '0');
INSERT INTO `character_skills` VALUES ('268498107', '253', '2', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1001', '2', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1007', '1', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1010', '1', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1090', '2', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1092', '1', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1095', '2', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1097', '2', '0');
INSERT INTO `character_skills` VALUES ('268498107', '1100', '2', '0');
INSERT INTO `character_skills` VALUES ('268498118', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268498118', '15', '3', '0');
INSERT INTO `character_skills` VALUES ('268498118', '16', '12', '0');
INSERT INTO `character_skills` VALUES ('268498118', '27', '1', '0');
INSERT INTO `character_skills` VALUES ('268498118', '56', '12', '0');
INSERT INTO `character_skills` VALUES ('268498118', '58', '6', '0');
INSERT INTO `character_skills` VALUES ('268498118', '77', '1', '0');
INSERT INTO `character_skills` VALUES ('268498118', '91', '2', '0');
INSERT INTO `character_skills` VALUES ('268498118', '113', '1', '0');
INSERT INTO `character_skills` VALUES ('268498118', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268498118', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268498118', '173', '1', '0');
INSERT INTO `character_skills` VALUES ('268498118', '195', '1', '0');
INSERT INTO `character_skills` VALUES ('268498118', '208', '3', '0');
INSERT INTO `character_skills` VALUES ('268498118', '209', '1', '0');
INSERT INTO `character_skills` VALUES ('268498118', '233', '2', '0');
INSERT INTO `character_skills` VALUES ('268498118', '312', '1', '0');
INSERT INTO `character_skills` VALUES ('268498467', '34', '1', '0');
INSERT INTO `character_skills` VALUES ('268498467', '42', '1', '0');
INSERT INTO `character_skills` VALUES ('268498467', '100', '15', '0');
INSERT INTO `character_skills` VALUES ('268498467', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268498467', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268498467', '148', '2', '0');
INSERT INTO `character_skills` VALUES ('268498467', '150', '2', '0');
INSERT INTO `character_skills` VALUES ('268498467', '172', '1', '0');
INSERT INTO `character_skills` VALUES ('268498467', '205', '8', '0');
INSERT INTO `character_skills` VALUES ('268498467', '211', '3', '0');
INSERT INTO `character_skills` VALUES ('268498467', '212', '2', '0');
INSERT INTO `character_skills` VALUES ('268498467', '216', '8', '0');
INSERT INTO `character_skills` VALUES ('268498467', '227', '13', '0');
INSERT INTO `character_skills` VALUES ('268498467', '231', '13', '0');
INSERT INTO `character_skills` VALUES ('268498467', '245', '15', '0');
INSERT INTO `character_skills` VALUES ('268498467', '254', '4', '0');
INSERT INTO `character_skills` VALUES ('268498467', '302', '2', '0');
INSERT INTO `character_skills` VALUES ('268498467', '444', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '146', '10', '0');
INSERT INTO `character_skills` VALUES ('268503745', '164', '2', '0');
INSERT INTO `character_skills` VALUES ('268503745', '213', '2', '0');
INSERT INTO `character_skills` VALUES ('268503745', '228', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '229', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '235', '6', '0');
INSERT INTO `character_skills` VALUES ('268503745', '236', '6', '0');
INSERT INTO `character_skills` VALUES ('268503745', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268503745', '249', '7', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1011', '15', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1015', '12', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1016', '2', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1027', '12', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1031', '6', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1035', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1040', '2', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1043', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1068', '2', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1069', '6', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1073', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1077', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1085', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1147', '2', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1164', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1168', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1184', '4', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1191', '1', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1201', '6', '0');
INSERT INTO `character_skills` VALUES ('268503745', '1204', '2', '0');
INSERT INTO `character_skills` VALUES ('268504379', '146', '21', '0');
INSERT INTO `character_skills` VALUES ('268504379', '164', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '212', '2', '0');
INSERT INTO `character_skills` VALUES ('268504379', '213', '4', '0');
INSERT INTO `character_skills` VALUES ('268504379', '228', '2', '0');
INSERT INTO `character_skills` VALUES ('268504379', '229', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '234', '17', '0');
INSERT INTO `character_skills` VALUES ('268504379', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '249', '18', '0');
INSERT INTO `character_skills` VALUES ('268504379', '285', '14', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1047', '2', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1056', '1', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1069', '18', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1071', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1072', '1', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1126', '6', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1127', '12', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1145', '1', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1164', '8', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1169', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1175', '8', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1182', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1183', '1', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1206', '5', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1223', '4', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1226', '4', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1227', '4', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1231', '6', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1235', '6', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1236', '6', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1237', '4', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1238', '2', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1264', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1265', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1274', '4', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1275', '3', '0');
INSERT INTO `character_skills` VALUES ('268504379', '1297', '2', '0');
INSERT INTO `character_skills` VALUES ('268509776', '34', '2', '0');
INSERT INTO `character_skills` VALUES ('268509776', '36', '9', '0');
INSERT INTO `character_skills` VALUES ('268509776', '42', '1', '0');
INSERT INTO `character_skills` VALUES ('268509776', '60', '1', '0');
INSERT INTO `character_skills` VALUES ('268509776', '100', '15', '0');
INSERT INTO `character_skills` VALUES ('268509776', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268509776', '148', '4', '0');
INSERT INTO `character_skills` VALUES ('268509776', '150', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '172', '1', '0');
INSERT INTO `character_skills` VALUES ('268509776', '205', '17', '0');
INSERT INTO `character_skills` VALUES ('268509776', '211', '4', '0');
INSERT INTO `character_skills` VALUES ('268509776', '212', '4', '0');
INSERT INTO `character_skills` VALUES ('268509776', '216', '17', '0');
INSERT INTO `character_skills` VALUES ('268509776', '227', '22', '0');
INSERT INTO `character_skills` VALUES ('268509776', '231', '22', '0');
INSERT INTO `character_skills` VALUES ('268509776', '245', '15', '0');
INSERT INTO `character_skills` VALUES ('268509776', '254', '5', '0');
INSERT INTO `character_skills` VALUES ('268509776', '260', '9', '0');
INSERT INTO `character_skills` VALUES ('268509776', '302', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '370', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '371', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '372', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '373', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '374', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '375', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '376', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '377', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '378', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '379', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '380', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '381', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '382', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '383', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '384', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '385', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '386', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '387', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '388', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '389', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '390', '3', '0');
INSERT INTO `character_skills` VALUES ('268509776', '391', '1', '0');
INSERT INTO `character_skills` VALUES ('268523291', '25', '3', '0');
INSERT INTO `character_skills` VALUES ('268523291', '34', '2', '0');
INSERT INTO `character_skills` VALUES ('268523291', '36', '9', '0');
INSERT INTO `character_skills` VALUES ('268523291', '42', '1', '0');
INSERT INTO `character_skills` VALUES ('268523291', '100', '15', '0');
INSERT INTO `character_skills` VALUES ('268523291', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268523291', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268523291', '148', '4', '0');
INSERT INTO `character_skills` VALUES ('268523291', '150', '3', '0');
INSERT INTO `character_skills` VALUES ('268523291', '172', '5', '0');
INSERT INTO `character_skills` VALUES ('268523291', '205', '17', '0');
INSERT INTO `character_skills` VALUES ('268523291', '211', '4', '0');
INSERT INTO `character_skills` VALUES ('268523291', '212', '4', '0');
INSERT INTO `character_skills` VALUES ('268523291', '216', '17', '0');
INSERT INTO `character_skills` VALUES ('268523291', '227', '22', '0');
INSERT INTO `character_skills` VALUES ('268523291', '231', '22', '0');
INSERT INTO `character_skills` VALUES ('268523291', '245', '15', '0');
INSERT INTO `character_skills` VALUES ('268523291', '248', '2', '0');
INSERT INTO `character_skills` VALUES ('268523291', '254', '1', '0');
INSERT INTO `character_skills` VALUES ('268523291', '260', '9', '0');
INSERT INTO `character_skills` VALUES ('268543578', '146', '12', '0');
INSERT INTO `character_skills` VALUES ('268543578', '164', '2', '0');
INSERT INTO `character_skills` VALUES ('268543578', '212', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '213', '2', '0');
INSERT INTO `character_skills` VALUES ('268543578', '228', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '229', '2', '0');
INSERT INTO `character_skills` VALUES ('268543578', '234', '8', '0');
INSERT INTO `character_skills` VALUES ('268543578', '244', '3', '0');
INSERT INTO `character_skills` VALUES ('268543578', '249', '9', '0');
INSERT INTO `character_skills` VALUES ('268543578', '285', '8', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1011', '6', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1012', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1015', '3', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1027', '3', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1040', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1068', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1069', '9', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1078', '2', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1126', '6', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1127', '12', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1128', '4', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1146', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1147', '6', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1151', '2', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1157', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1160', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1167', '2', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1168', '3', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1172', '8', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1177', '5', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1178', '8', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1181', '3', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1184', '6', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1206', '5', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1222', '1', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1224', '3', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1228', '4', '0');
INSERT INTO `character_skills` VALUES ('268543578', '1266', '3', '0');
INSERT INTO `character_skills` VALUES ('268631868', '1', '37', '0');
INSERT INTO `character_skills` VALUES ('268631868', '3', '9', '0');
INSERT INTO `character_skills` VALUES ('268631868', '5', '31', '0');
INSERT INTO `character_skills` VALUES ('268631868', '6', '37', '0');
INSERT INTO `character_skills` VALUES ('268631868', '7', '28', '0');
INSERT INTO `character_skills` VALUES ('268631868', '8', '7', '0');
INSERT INTO `character_skills` VALUES ('268631868', '9', '34', '0');
INSERT INTO `character_skills` VALUES ('268631868', '16', '9', '0');
INSERT INTO `character_skills` VALUES ('268631868', '56', '9', '0');
INSERT INTO `character_skills` VALUES ('268631868', '75', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '78', '2', '0');
INSERT INTO `character_skills` VALUES ('268631868', '80', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '87', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '88', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '100', '15', '0');
INSERT INTO `character_skills` VALUES ('268631868', '104', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '121', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '141', '3', '0');
INSERT INTO `character_skills` VALUES ('268631868', '142', '5', '0');
INSERT INTO `character_skills` VALUES ('268631868', '144', '37', '0');
INSERT INTO `character_skills` VALUES ('268631868', '148', '2', '0');
INSERT INTO `character_skills` VALUES ('268631868', '190', '37', '0');
INSERT INTO `character_skills` VALUES ('268631868', '211', '3', '0');
INSERT INTO `character_skills` VALUES ('268631868', '212', '8', '0');
INSERT INTO `character_skills` VALUES ('268631868', '216', '8', '0');
INSERT INTO `character_skills` VALUES ('268631868', '226', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '227', '50', '0');
INSERT INTO `character_skills` VALUES ('268631868', '231', '50', '0');
INSERT INTO `character_skills` VALUES ('268631868', '245', '15', '0');
INSERT INTO `character_skills` VALUES ('268631868', '255', '15', '0');
INSERT INTO `character_skills` VALUES ('268631868', '256', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '257', '45', '0');
INSERT INTO `character_skills` VALUES ('268631868', '260', '37', '0');
INSERT INTO `character_skills` VALUES ('268631868', '261', '22', '0');
INSERT INTO `character_skills` VALUES ('268631868', '287', '3', '0');
INSERT INTO `character_skills` VALUES ('268631868', '290', '14', '0');
INSERT INTO `character_skills` VALUES ('268631868', '297', '2', '0');
INSERT INTO `character_skills` VALUES ('268631868', '312', '20', '0');
INSERT INTO `character_skills` VALUES ('268631868', '328', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '329', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '330', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '340', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '345', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '359', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '360', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '424', '3', '0');
INSERT INTO `character_skills` VALUES ('268631868', '426', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '430', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '440', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '442', '1', '0');
INSERT INTO `character_skills` VALUES ('268631868', '451', '2', '0');

-- ----------------------------
-- Table structure for `character_skills_save`
-- ----------------------------
DROP TABLE IF EXISTS `character_skills_save`;
CREATE TABLE `character_skills_save` (
  `char_obj_id` int(11) NOT NULL DEFAULT 0,
  `skill_id` int(11) NOT NULL DEFAULT 0,
  `skill_level` int(3) NOT NULL DEFAULT 1,
  `effect_count` int(11) NOT NULL DEFAULT 0,
  `effect_cur_time` int(11) NOT NULL DEFAULT 0,
  `reuse_delay` int(8) NOT NULL DEFAULT 0,
  `systime` bigint(20) unsigned NOT NULL DEFAULT 0,
  `restore_type` int(1) NOT NULL DEFAULT 0,
  `class_index` int(1) NOT NULL DEFAULT 0,
  `buff_index` int(2) NOT NULL DEFAULT 0,
  `npc` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`skill_id`,`skill_level`,`class_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_skills_save
-- ----------------------------
INSERT INTO `character_skills_save` VALUES ('268493285', '264', '1', '1', '772', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '267', '1', '1', '772', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '268', '1', '1', '772', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '269', '1', '1', '772', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '271', '1', '1', '772', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '274', '1', '1', '772', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '275', '1', '1', '772', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '304', '1', '1', '772', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '349', '1', '1', '772', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '364', '1', '1', '772', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1035', '1', '1', '772', '0', '0', '0', '0', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1036', '1', '1', '772', '0', '0', '0', '0', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1040', '1', '1', '772', '0', '0', '0', '0', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1045', '1', '1', '772', '0', '0', '0', '0', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1068', '1', '1', '772', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1077', '1', '1', '772', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1086', '1', '1', '772', '0', '0', '0', '0', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1204', '1', '1', '772', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1240', '1', '1', '772', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1242', '1', '1', '772', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1363', '1', '1', '772', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '1391', '1', '1', '772', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '4699', '2', '1', '772', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268493285', '4703', '2', '1', '772', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '264', '1', '1', '1573', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '267', '1', '1', '1573', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '268', '1', '1', '1573', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '269', '1', '1', '1573', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '271', '1', '1', '1573', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '274', '1', '1', '1573', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '275', '1', '1', '1573', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '304', '1', '1', '1573', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '349', '1', '1', '1573', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '364', '1', '1', '1573', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1035', '1', '1', '1573', '0', '0', '0', '0', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1036', '1', '1', '1573', '0', '0', '0', '0', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1040', '1', '1', '1573', '0', '0', '0', '0', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1045', '1', '1', '1573', '0', '0', '0', '0', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1068', '1', '1', '1573', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1077', '1', '1', '1573', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1086', '1', '1', '1573', '0', '0', '0', '0', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1204', '1', '1', '1573', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1240', '1', '1', '1573', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1242', '1', '1', '1573', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1363', '1', '1', '1573', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '1391', '1', '1', '1573', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '4699', '2', '1', '1573', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268493302', '4703', '2', '1', '1573', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268493309', '264', '1', '1', '4704', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '267', '1', '1', '4704', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '268', '1', '1', '4704', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '273', '1', '1', '4704', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '276', '1', '1', '4704', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '304', '1', '1', '4704', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '349', '1', '1', '4704', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '365', '1', '1', '4704', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1035', '4', '1', '4704', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1036', '2', '1', '4704', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1040', '3', '1', '4704', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1044', '3', '1', '4704', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1045', '6', '1', '4704', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1059', '3', '1', '4704', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1062', '2', '1', '4704', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1078', '6', '1', '4704', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1085', '3', '1', '4704', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1204', '2', '1', '4704', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1243', '6', '1', '4704', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1303', '2', '1', '4704', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1304', '3', '1', '4704', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1323', '1', '1', '2271', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268493309', '1389', '3', '1', '4704', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268493309', '1413', '1', '1', '4704', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '264', '1', '1', '788', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '267', '1', '1', '788', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '268', '1', '1', '788', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '269', '1', '1', '788', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '271', '1', '1', '788', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '274', '1', '1', '788', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '275', '1', '1', '788', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '310', '1', '1', '788', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '364', '1', '1', '788', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1035', '4', '1', '788', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1036', '2', '1', '788', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1040', '3', '1', '788', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1045', '6', '1', '788', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1062', '2', '1', '788', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1068', '3', '1', '788', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1077', '3', '1', '788', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1086', '2', '1', '788', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1204', '2', '1', '788', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1242', '3', '1', '788', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1259', '4', '1', '788', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1268', '4', '1', '788', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1363', '1', '1', '788', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '1388', '3', '1', '788', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268493578', '4699', '13', '1', '788', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '99', '1', '1', '334', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268493605', '264', '1', '1', '208', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '266', '1', '1', '208', '0', '0', '0', '0', '25', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '267', '1', '1', '208', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '268', '1', '1', '208', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '269', '1', '1', '208', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '271', '1', '1', '208', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '272', '1', '1', '208', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '274', '1', '1', '208', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '275', '1', '1', '208', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '304', '1', '1', '208', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '364', '1', '1', '208', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1036', '2', '1', '208', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1040', '3', '1', '208', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1045', '6', '1', '208', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1062', '2', '1', '208', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1068', '3', '1', '208', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1077', '3', '1', '208', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1086', '2', '1', '208', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1204', '2', '1', '208', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1240', '3', '1', '208', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1242', '3', '1', '208', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1363', '1', '1', '208', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '1388', '3', '1', '208', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268493605', '4699', '13', '1', '349', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '264', '1', '1', '98', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '267', '1', '1', '98', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '268', '1', '1', '98', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '269', '1', '1', '98', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '271', '1', '1', '98', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '274', '1', '1', '98', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '275', '1', '1', '98', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '310', '1', '1', '98', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '364', '1', '1', '98', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1035', '4', '1', '98', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1036', '2', '1', '98', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1040', '3', '1', '98', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1045', '6', '1', '98', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1062', '2', '1', '98', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1068', '3', '1', '98', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1077', '3', '1', '98', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1086', '2', '1', '98', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1204', '2', '1', '98', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1242', '3', '1', '98', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1259', '4', '1', '98', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1268', '4', '1', '98', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1363', '1', '1', '98', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '1388', '3', '1', '98', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268493707', '4073', '4', '1', '4', '0', '0', '0', '0', '25', '0');
INSERT INTO `character_skills_save` VALUES ('268493707', '4699', '13', '1', '98', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '264', '1', '1', '6137', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '267', '1', '1', '6213', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '268', '1', '1', '6137', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '269', '1', '1', '6137', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '271', '1', '1', '6137', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '274', '1', '1', '6137', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '275', '1', '1', '6137', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '310', '1', '1', '6137', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '364', '1', '1', '6137', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1036', '2', '1', '6137', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1040', '3', '1', '6137', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1045', '6', '1', '6137', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1062', '2', '1', '6137', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1068', '3', '1', '6137', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1077', '3', '1', '6137', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1086', '2', '1', '6137', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1204', '2', '1', '6137', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1242', '3', '1', '6137', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1243', '6', '1', '6137', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1268', '4', '1', '6137', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1304', '3', '1', '6137', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1363', '1', '1', '6137', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '1388', '3', '1', '6137', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '4554', '4', '1', '6137', '0', '0', '0', '0', '25', '1');
INSERT INTO `character_skills_save` VALUES ('268493896', '4700', '13', '1', '6137', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '264', '1', '1', '76', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '266', '1', '1', '76', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '267', '1', '1', '76', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '268', '1', '1', '76', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '269', '1', '1', '76', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '271', '1', '1', '76', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '274', '1', '1', '76', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '275', '1', '1', '76', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '304', '1', '1', '76', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '310', '1', '1', '76', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1040', '3', '1', '76', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1045', '6', '1', '76', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1062', '2', '1', '76', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1068', '3', '1', '76', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1077', '3', '1', '76', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1086', '2', '1', '76', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1087', '3', '1', '76', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1204', '2', '1', '76', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1240', '3', '1', '76', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1242', '3', '1', '76', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1268', '4', '1', '76', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1323', '1', '1', '64', '0', '0', '0', '0', '25', '0');
INSERT INTO `character_skills_save` VALUES ('268493913', '1363', '1', '1', '76', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '1388', '3', '1', '76', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268493913', '4700', '13', '1', '76', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268493980', '264', '1', '1', '975', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '267', '1', '1', '975', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '268', '1', '1', '975', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '273', '1', '1', '975', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '276', '1', '1', '975', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '349', '1', '1', '975', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '365', '1', '1', '975', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1035', '1', '1', '975', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1036', '1', '1', '975', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1040', '1', '1', '975', '0', '0', '0', '0', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1045', '1', '1', '975', '0', '0', '0', '0', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1048', '1', '1', '975', '0', '0', '0', '0', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1059', '1', '1', '975', '0', '0', '0', '0', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1062', '1', '1', '975', '0', '0', '0', '0', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1078', '1', '1', '975', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1085', '1', '1', '975', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1204', '1', '1', '975', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1243', '1', '1', '975', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1259', '1', '1', '975', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1303', '1', '1', '975', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1389', '1', '1', '975', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1397', '1', '1', '975', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '1413', '1', '1', '975', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268493980', '4703', '1', '1', '975', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268494184', '264', '1', '1', '1201', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '267', '1', '1', '1201', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '268', '1', '1', '1201', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '269', '1', '1', '1201', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '271', '1', '1', '1201', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '274', '1', '1', '1201', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '275', '1', '1', '1201', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '310', '1', '1', '1201', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '364', '1', '1', '1201', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1035', '4', '1', '1201', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1036', '2', '1', '1201', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1040', '3', '1', '1201', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1045', '6', '1', '1201', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1062', '2', '1', '1201', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1068', '3', '1', '1201', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1077', '3', '1', '1201', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1086', '2', '1', '1201', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1204', '2', '1', '1201', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1242', '3', '1', '1201', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1259', '4', '1', '1201', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1268', '4', '1', '1201', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1363', '1', '1', '1201', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '1388', '3', '1', '1201', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268494184', '4699', '13', '1', '1201', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '264', '1', '1', '39', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '267', '1', '1', '39', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '268', '1', '1', '39', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '273', '1', '1', '39', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '276', '1', '1', '39', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '349', '1', '1', '39', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '365', '1', '1', '39', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1035', '4', '1', '39', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1036', '2', '1', '39', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1040', '3', '1', '39', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1045', '6', '1', '39', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1048', '6', '1', '39', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1059', '3', '1', '39', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1062', '2', '1', '39', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1078', '6', '1', '39', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1085', '3', '1', '39', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1204', '2', '1', '39', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1243', '6', '1', '39', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1259', '4', '1', '39', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1303', '2', '1', '39', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1355', '1', '1', '39', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1389', '3', '1', '39', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '1397', '3', '1', '39', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268494228', '4703', '13', '1', '39', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268494356', '264', '1', '1', '2394', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '267', '1', '1', '2392', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '268', '1', '1', '2391', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '269', '1', '1', '2391', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '271', '1', '1', '2405', '0', '0', '0', '0', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '272', '1', '1', '2405', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '274', '1', '1', '2404', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '275', '1', '1', '2404', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '305', '1', '1', '2388', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '310', '1', '1', '2400', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '364', '1', '1', '2384', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1062', '2', '1', '2428', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1068', '3', '1', '2425', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1077', '3', '1', '2422', '0', '0', '0', '0', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1086', '2', '1', '2424', '0', '0', '0', '0', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1240', '3', '1', '2422', '0', '0', '0', '0', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1243', '6', '1', '2331', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1259', '4', '1', '2324', '0', '0', '0', '0', '25', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1284', '3', '1', '2364', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1304', '3', '1', '2324', '0', '0', '0', '0', '26', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1310', '4', '1', '2359', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1362', '1', '1', '2356', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1363', '1', '1', '2358', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1364', '1', '1', '2373', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1388', '3', '1', '2416', '0', '0', '0', '0', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '1416', '1', '1', '2370', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268494356', '4700', '3', '1', '2313', '0', '0', '0', '0', '27', '0');
INSERT INTO `character_skills_save` VALUES ('268494406', '1323', '1', '1', '1033', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '4', '1', '1', '8', '0', '0', '0', '2', '28', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '87', '1', '1', '253', '0', '0', '0', '1', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '99', '1', '1', '152', '0', '0', '0', '2', '26', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '131', '2', '1', '133', '0', '0', '0', '2', '27', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '264', '1', '1', '6542', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '264', '1', '1', '205', '0', '0', '0', '2', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '267', '1', '1', '6542', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '267', '1', '1', '203', '0', '0', '0', '2', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '268', '1', '1', '6542', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '268', '1', '1', '201', '0', '0', '0', '2', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '269', '1', '1', '204', '0', '0', '0', '2', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '271', '1', '1', '211', '0', '0', '0', '2', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '272', '1', '1', '212', '0', '0', '0', '2', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '273', '1', '1', '6542', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '274', '1', '1', '214', '0', '0', '0', '2', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '275', '1', '1', '213', '0', '0', '0', '2', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '276', '1', '1', '6542', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '304', '1', '1', '3599', '0', '0', '0', '1', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '304', '1', '1', '203', '0', '0', '0', '2', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '305', '1', '1', '3598', '0', '0', '0', '1', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '305', '1', '1', '204', '0', '0', '0', '2', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '310', '1', '1', '212', '0', '0', '0', '2', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '349', '1', '1', '6542', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '364', '1', '1', '3595', '0', '0', '0', '1', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '364', '1', '1', '938', '0', '0', '0', '2', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '365', '1', '1', '6542', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1002', '3', '1', '308', '0', '0', '0', '2', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1035', '4', '1', '6542', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1036', '2', '1', '6542', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1036', '2', '1', '3468', '0', '0', '0', '1', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1036', '2', '1', '208', '0', '0', '0', '2', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1040', '2', '1', '206', '0', '0', '0', '2', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1040', '3', '1', '6542', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1040', '3', '1', '3467', '0', '0', '0', '1', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1045', '6', '1', '6542', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1045', '6', '1', '217', '0', '0', '0', '2', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1048', '6', '1', '6542', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1059', '3', '1', '6542', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1062', '2', '1', '6542', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1062', '2', '1', '3401', '0', '0', '0', '1', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1078', '6', '1', '6542', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1085', '3', '1', '6542', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1086', '2', '1', '209', '0', '0', '0', '2', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1204', '2', '1', '6542', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1240', '3', '1', '209', '0', '0', '0', '2', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1243', '6', '1', '6535', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1259', '4', '1', '6509', '0', '0', '0', '0', '25', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1259', '4', '1', '3529', '0', '0', '0', '1', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1284', '3', '1', '6510', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1284', '3', '1', '3589', '0', '0', '0', '1', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1284', '3', '1', '217', '0', '0', '0', '2', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1303', '2', '1', '6542', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1304', '3', '1', '6534', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1310', '4', '1', '3586', '0', '0', '0', '1', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1323', '1', '1', '3575', '0', '0', '0', '1', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1323', '1', '1', '208', '0', '0', '0', '2', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1363', '1', '1', '3584', '0', '0', '0', '1', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1363', '1', '1', '215', '0', '0', '0', '2', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1364', '1', '1', '3571', '0', '0', '0', '1', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1364', '1', '1', '199', '0', '0', '0', '2', '25', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1388', '3', '1', '211', '0', '0', '0', '2', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '1389', '3', '1', '6542', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1397', '3', '1', '6542', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1413', '1', '1', '6533', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '1416', '1', '1', '932', '0', '0', '0', '2', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '4553', '4', '1', '930', '0', '0', '0', '2', '29', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '4554', '4', '1', '6518', '0', '0', '0', '0', '27', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '4699', '3', '1', '3545', '0', '0', '0', '1', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268494667', '4699', '13', '1', '929', '0', '0', '0', '2', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268494667', '4703', '13', '1', '6505', '0', '0', '0', '0', '26', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '264', '1', '1', '4312', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '267', '1', '1', '4312', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '268', '1', '1', '4312', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '273', '1', '1', '4312', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '276', '1', '1', '4312', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '349', '1', '1', '4312', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '365', '1', '1', '4312', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1035', '4', '1', '4312', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1036', '2', '1', '4312', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1040', '3', '1', '4312', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1045', '6', '1', '4312', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1048', '6', '1', '4312', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1059', '3', '1', '4312', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1062', '2', '1', '4312', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1078', '6', '1', '4312', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1085', '3', '1', '4312', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1204', '2', '1', '4312', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1243', '6', '1', '4312', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1259', '4', '1', '4312', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1303', '2', '1', '4312', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1355', '1', '1', '4312', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1389', '3', '1', '4312', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '1397', '3', '1', '4312', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268494751', '4703', '13', '1', '4312', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268495632', '264', '1', '1', '2144', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '267', '1', '1', '2144', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '268', '1', '1', '2144', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '269', '1', '1', '2144', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '271', '1', '1', '2144', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '274', '1', '1', '2144', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '275', '1', '1', '2144', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '304', '1', '1', '2144', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '349', '1', '1', '2144', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '364', '1', '1', '2144', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1035', '1', '1', '2144', '0', '0', '0', '0', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1036', '1', '1', '2144', '0', '0', '0', '0', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1040', '1', '1', '2144', '0', '0', '0', '0', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1045', '1', '1', '2144', '0', '0', '0', '0', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1068', '1', '1', '2144', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1077', '1', '1', '2144', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1086', '1', '1', '2144', '0', '0', '0', '0', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1204', '1', '1', '2144', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1240', '1', '1', '2144', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1242', '1', '1', '2144', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1363', '1', '1', '2144', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '1391', '1', '1', '2144', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '4699', '2', '1', '2144', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268495632', '4703', '2', '1', '2144', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268497182', '264', '1', '1', '320', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '267', '1', '1', '320', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '268', '1', '1', '320', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '269', '1', '1', '320', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '271', '1', '1', '320', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '274', '1', '1', '320', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '275', '1', '1', '320', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '310', '1', '1', '320', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '364', '1', '1', '320', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1035', '4', '1', '320', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1036', '2', '1', '320', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1040', '3', '1', '320', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1045', '6', '1', '320', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1062', '2', '1', '320', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1068', '3', '1', '320', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1077', '3', '1', '320', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1086', '2', '1', '320', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1204', '2', '1', '320', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1242', '3', '1', '320', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1259', '4', '1', '320', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1268', '4', '1', '320', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1363', '1', '1', '320', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '1388', '3', '1', '320', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268497182', '4699', '13', '1', '320', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '94', '2', '1', '63', '91892', '1777433926569', '0', '0', '26', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '264', '1', '1', '237', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '267', '1', '1', '236', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '268', '1', '1', '233', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '269', '1', '1', '237', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '271', '1', '1', '246', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '272', '1', '1', '249', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '274', '1', '1', '252', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '275', '1', '1', '250', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '304', '1', '1', '236', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '305', '1', '1', '237', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '310', '1', '1', '247', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '364', '1', '1', '1451', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '1036', '2', '1', '241', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '1045', '6', '1', '256', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '1068', '3', '1', '1451', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '1077', '3', '1', '1451', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '1086', '2', '1', '242', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '1204', '2', '1', '1451', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '1259', '4', '1', '1451', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '1268', '4', '1', '1451', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '1284', '3', '1', '255', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '1323', '1', '1', '49', '0', '0', '0', '0', '27', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '1363', '1', '1', '253', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '1364', '1', '1', '224', '0', '0', '0', '0', '25', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '1388', '3', '1', '245', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268497394', '4553', '4', '1', '192', '0', '0', '0', '0', '28', '1');
INSERT INTO `character_skills_save` VALUES ('268497394', '4699', '13', '1', '1451', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '264', '1', '1', '269', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '267', '1', '1', '269', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '268', '1', '1', '269', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '273', '1', '1', '269', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '276', '1', '1', '269', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '349', '1', '1', '269', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '365', '1', '1', '269', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1035', '4', '1', '269', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1036', '2', '1', '269', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1040', '3', '1', '269', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1045', '6', '1', '269', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1048', '6', '1', '269', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1059', '3', '1', '269', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1062', '2', '1', '269', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1078', '6', '1', '269', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1085', '3', '1', '269', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1204', '2', '1', '269', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1243', '6', '1', '269', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1259', '4', '1', '269', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1303', '2', '1', '269', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1355', '1', '1', '269', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1389', '3', '1', '269', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '1397', '3', '1', '269', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268497927', '4703', '13', '1', '269', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268498074', '1204', '2', '1', '3328', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268498107', '264', '1', '1', '1243', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '267', '1', '1', '1244', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '268', '1', '1', '1247', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '273', '1', '1', '1259', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '276', '1', '1', '1258', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '304', '1', '1', '1239', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '305', '1', '1', '1238', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '349', '1', '1', '1778', '0', '0', '0', '0', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '365', '1', '1', '1253', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1035', '4', '1', '1294', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1036', '2', '1', '1294', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1040', '3', '1', '1294', '0', '0', '0', '0', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1045', '6', '1', '1296', '0', '0', '0', '0', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1062', '2', '1', '1297', '0', '0', '0', '0', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1085', '3', '1', '1289', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1204', '1', '1', '1778', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1243', '6', '1', '1194', '0', '0', '0', '0', '25', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1259', '4', '1', '1189', '0', '0', '0', '0', '27', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1284', '3', '1', '1228', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1303', '1', '1', '1778', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1304', '3', '1', '1191', '0', '0', '0', '0', '26', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1323', '1', '1', '1211', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1362', '1', '1', '1224', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1389', '3', '1', '1281', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1397', '1', '1', '1778', '0', '0', '0', '0', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '1413', '1', '1', '1219', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268498107', '4703', '3', '1', '1267', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '264', '1', '1', '1774', '0', '0', '0', '0', '11', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '267', '1', '1', '1774', '0', '0', '0', '0', '12', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '268', '1', '1', '1774', '0', '0', '0', '0', '13', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '269', '1', '1', '1774', '0', '0', '0', '0', '14', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '271', '1', '1', '1774', '0', '0', '0', '0', '18', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '274', '1', '1', '1774', '0', '0', '0', '0', '19', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '275', '1', '1', '1774', '0', '0', '0', '0', '20', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '304', '1', '1', '1774', '0', '0', '0', '0', '15', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '349', '1', '1', '1774', '0', '0', '0', '0', '16', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '364', '1', '1', '1774', '0', '0', '0', '0', '17', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1035', '1', '1', '1774', '0', '0', '0', '0', '4', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1036', '1', '1', '1774', '0', '0', '0', '0', '5', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1040', '1', '1', '1774', '0', '0', '0', '0', '3', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1045', '1', '1', '1774', '0', '0', '0', '0', '6', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1068', '1', '1', '1774', '0', '0', '0', '0', '2', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1077', '1', '1', '1774', '0', '0', '0', '0', '8', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1086', '1', '1', '1774', '0', '0', '0', '0', '7', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1204', '1', '1', '1774', '0', '0', '0', '0', '1', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1240', '1', '1', '1774', '0', '0', '0', '0', '9', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1242', '1', '1', '1774', '0', '0', '0', '0', '10', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1363', '1', '1', '1774', '0', '0', '0', '0', '21', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '1391', '1', '1', '1774', '0', '0', '0', '0', '22', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '4699', '2', '1', '1774', '0', '0', '0', '0', '23', '0');
INSERT INTO `character_skills_save` VALUES ('268498118', '4703', '2', '1', '1774', '0', '0', '0', '0', '24', '0');
INSERT INTO `character_skills_save` VALUES ('268498467', '264', '1', '1', '303745', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '267', '1', '1', '303745', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '268', '1', '1', '303745', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '269', '1', '1', '303745', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '271', '1', '1', '303745', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '274', '1', '1', '303745', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '275', '1', '1', '303745', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '310', '1', '1', '303745', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '364', '1', '1', '303745', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1035', '4', '1', '303745', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1036', '2', '1', '303745', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1040', '3', '1', '303745', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1045', '6', '1', '303745', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1062', '2', '1', '303745', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1068', '3', '1', '303745', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1077', '3', '1', '303745', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1086', '2', '1', '303745', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1204', '2', '1', '303745', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1242', '3', '1', '303745', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1259', '4', '1', '303745', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1268', '4', '1', '303745', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1363', '1', '1', '303745', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '1388', '3', '1', '303745', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268498467', '4699', '13', '1', '303745', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268503745', '4703', '13', '0', '17250', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '264', '1', '1', '1334', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '267', '1', '1', '1334', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '268', '1', '1', '1334', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '273', '1', '1', '1334', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '276', '1', '1', '1334', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '349', '1', '1', '1334', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '365', '1', '1', '1334', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1035', '4', '1', '1334', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1036', '2', '1', '1334', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1040', '3', '1', '1334', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1045', '6', '1', '1334', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1048', '6', '1', '1334', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1059', '3', '1', '1318', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1062', '2', '1', '1334', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1078', '6', '1', '1334', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1085', '3', '1', '1317', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1204', '2', '1', '1334', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1243', '6', '1', '1322', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1259', '4', '1', '1334', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1303', '2', '1', '1334', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1304', '3', '1', '1324', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1323', '1', '1', '49', '0', '0', '0', '0', '26', '0');
INSERT INTO `character_skills_save` VALUES ('268504379', '1389', '3', '1', '1318', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1397', '3', '1', '1334', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '1413', '1', '1', '1314', '0', '0', '0', '0', '25', '1');
INSERT INTO `character_skills_save` VALUES ('268504379', '4703', '13', '1', '1334', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '264', '1', '1', '144', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '267', '1', '1', '144', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '268', '1', '1', '144', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '269', '1', '1', '144', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '271', '1', '1', '144', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '274', '1', '1', '144', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '275', '1', '1', '144', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '310', '1', '1', '144', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '364', '1', '1', '144', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1035', '4', '1', '144', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1036', '2', '1', '144', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1040', '3', '1', '144', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1045', '6', '1', '144', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1062', '2', '1', '144', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1068', '3', '1', '144', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1077', '3', '1', '144', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1086', '2', '1', '144', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1204', '2', '1', '144', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1242', '3', '1', '144', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1259', '4', '1', '144', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1268', '4', '1', '144', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1363', '1', '1', '144', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '1388', '3', '1', '144', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268509776', '4699', '13', '1', '144', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '264', '1', '1', '2628', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '267', '1', '1', '2628', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '268', '1', '1', '2628', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '269', '1', '1', '2628', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '271', '1', '1', '2628', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '274', '1', '1', '2628', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '275', '1', '1', '2628', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '310', '1', '1', '2628', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '364', '1', '1', '2628', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1035', '4', '1', '2628', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1036', '2', '1', '2628', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1040', '3', '1', '2628', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1045', '6', '1', '2628', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1062', '2', '1', '2628', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1068', '3', '1', '2628', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1077', '3', '1', '2628', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1086', '2', '1', '2628', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1204', '2', '1', '2628', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1242', '3', '1', '2628', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1259', '4', '1', '2628', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1268', '4', '1', '2628', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1363', '1', '1', '2628', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '1388', '3', '1', '2628', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268523291', '4699', '13', '1', '2628', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '264', '1', '1', '9', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '267', '1', '1', '9', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '268', '1', '1', '9', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '273', '1', '1', '9', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '276', '1', '1', '9', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '349', '1', '1', '9', '0', '0', '0', '0', '23', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '365', '1', '1', '9', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1035', '4', '1', '9', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1036', '2', '1', '9', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1040', '3', '1', '9', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1045', '6', '1', '9', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1048', '6', '1', '9', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1059', '3', '1', '9', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1062', '2', '1', '9', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1078', '6', '1', '9', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1085', '3', '1', '9', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1204', '2', '1', '9', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1243', '6', '1', '9', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1259', '4', '1', '9', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1303', '2', '1', '9', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1355', '1', '1', '9', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1389', '3', '1', '9', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '1397', '3', '1', '9', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268543578', '4703', '13', '1', '9', '0', '0', '0', '0', '24', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '264', '1', '1', '902', '0', '0', '0', '0', '18', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '267', '1', '1', '902', '0', '0', '0', '0', '19', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '268', '1', '1', '902', '0', '0', '0', '0', '20', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '269', '1', '1', '902', '0', '0', '0', '0', '21', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '271', '1', '1', '902', '0', '0', '0', '0', '14', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '274', '1', '1', '902', '0', '0', '0', '0', '15', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '275', '1', '1', '902', '0', '0', '0', '0', '16', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '310', '1', '1', '902', '0', '0', '0', '0', '17', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '364', '1', '1', '902', '0', '0', '0', '0', '22', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1036', '2', '1', '902', '0', '0', '0', '0', '1', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1040', '3', '1', '902', '0', '0', '0', '0', '2', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1045', '6', '1', '902', '0', '0', '0', '0', '3', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1062', '2', '1', '902', '0', '0', '0', '0', '4', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1068', '3', '1', '902', '0', '0', '0', '0', '5', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1077', '3', '1', '902', '0', '0', '0', '0', '6', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1086', '2', '1', '902', '0', '0', '0', '0', '7', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1204', '2', '1', '902', '0', '0', '0', '0', '8', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1242', '3', '1', '902', '0', '0', '0', '0', '9', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1259', '4', '1', '902', '0', '0', '0', '0', '10', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1268', '4', '1', '902', '0', '0', '0', '0', '11', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1363', '1', '1', '902', '0', '0', '0', '0', '12', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '1388', '3', '1', '902', '0', '0', '0', '0', '13', '1');
INSERT INTO `character_skills_save` VALUES ('268631868', '4699', '13', '1', '902', '0', '0', '0', '0', '23', '1');

-- ----------------------------
-- Table structure for `character_subclasses`
-- ----------------------------
DROP TABLE IF EXISTS `character_subclasses`;
CREATE TABLE `character_subclasses` (
  `char_obj_id` decimal(11,0) NOT NULL DEFAULT 0,
  `class_id` int(2) NOT NULL DEFAULT 0,
  `exp` decimal(20,0) NOT NULL DEFAULT 0,
  `sp` decimal(11,0) NOT NULL DEFAULT 0,
  `level` int(2) NOT NULL DEFAULT 40,
  `class_index` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`,`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of character_subclasses
-- ----------------------------
INSERT INTO `character_subclasses` VALUES ('268494667', '2', '1511275834', '2906183', '78', '1');
INSERT INTO `character_subclasses` VALUES ('268494667', '9', '51625911', '2961161', '52', '2');

-- ----------------------------
-- Table structure for `clanhall`
-- ----------------------------
DROP TABLE IF EXISTS `clanhall`;
CREATE TABLE `clanhall` (
  `id` tinyint(4) NOT NULL DEFAULT 0,
  `ownerId` int(10) unsigned NOT NULL DEFAULT 0,
  `paidUntil` bigint(20) NOT NULL DEFAULT 0,
  `paid` tinyint(4) NOT NULL DEFAULT 0,
  `sellerBid` int(10) unsigned NOT NULL DEFAULT 0,
  `sellerName` varchar(20) NOT NULL DEFAULT '',
  `sellerClanName` varchar(20) NOT NULL DEFAULT '',
  `endDate` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clanhall
-- ----------------------------
INSERT INTO `clanhall` VALUES ('21', '0', '0', '0', '0', '', '', '1780239600977');
INSERT INTO `clanhall` VALUES ('22', '268493856', '1779412646063', '1', '0', '', '', '1779630690027');
INSERT INTO `clanhall` VALUES ('23', '0', '0', '0', '0', '', '', '1779630690074');
INSERT INTO `clanhall` VALUES ('24', '0', '0', '0', '0', '', '', '1779630690173');
INSERT INTO `clanhall` VALUES ('25', '0', '0', '0', '0', '', '', '1779630690203');
INSERT INTO `clanhall` VALUES ('26', '0', '0', '0', '0', '', '', '1779630690242');
INSERT INTO `clanhall` VALUES ('27', '0', '0', '0', '0', '', '', '1779630690285');
INSERT INTO `clanhall` VALUES ('28', '0', '0', '0', '0', '', '', '1779630690333');
INSERT INTO `clanhall` VALUES ('29', '0', '0', '0', '0', '', '', '1779630690360');
INSERT INTO `clanhall` VALUES ('30', '0', '0', '0', '0', '', '', '1779630690408');
INSERT INTO `clanhall` VALUES ('31', '0', '0', '0', '0', '', '', '1779630690438');
INSERT INTO `clanhall` VALUES ('32', '0', '0', '0', '0', '', '', '1779630690462');
INSERT INTO `clanhall` VALUES ('33', '0', '0', '0', '0', '', '', '1779630690479');
INSERT INTO `clanhall` VALUES ('34', '0', '0', '0', '0', '', '', '1780239600497');
INSERT INTO `clanhall` VALUES ('35', '0', '0', '0', '0', '', '', '1780239600554');
INSERT INTO `clanhall` VALUES ('36', '0', '0', '0', '0', '', '', '1779630690575');
INSERT INTO `clanhall` VALUES ('37', '0', '0', '0', '0', '', '', '1779630690602');
INSERT INTO `clanhall` VALUES ('38', '0', '0', '0', '0', '', '', '1779630690631');
INSERT INTO `clanhall` VALUES ('39', '0', '0', '0', '0', '', '', '1779630690678');
INSERT INTO `clanhall` VALUES ('40', '0', '0', '0', '0', '', '', '1779630690712');
INSERT INTO `clanhall` VALUES ('41', '0', '0', '0', '0', '', '', '1779630690772');
INSERT INTO `clanhall` VALUES ('42', '0', '0', '0', '0', '', '', '1779630690785');
INSERT INTO `clanhall` VALUES ('43', '0', '0', '0', '0', '', '', '1779630690807');
INSERT INTO `clanhall` VALUES ('44', '0', '0', '0', '0', '', '', '1779630690820');
INSERT INTO `clanhall` VALUES ('45', '0', '0', '0', '0', '', '', '1779630690832');
INSERT INTO `clanhall` VALUES ('46', '0', '0', '0', '0', '', '', '1779630690850');
INSERT INTO `clanhall` VALUES ('47', '0', '0', '0', '0', '', '', '1779630690887');
INSERT INTO `clanhall` VALUES ('48', '0', '0', '0', '0', '', '', '1779630690895');
INSERT INTO `clanhall` VALUES ('49', '0', '0', '0', '0', '', '', '1779630690919');
INSERT INTO `clanhall` VALUES ('50', '0', '0', '0', '0', '', '', '1779630690966');
INSERT INTO `clanhall` VALUES ('51', '0', '0', '0', '0', '', '', '1779630691015');
INSERT INTO `clanhall` VALUES ('52', '0', '0', '0', '0', '', '', '1779630691050');
INSERT INTO `clanhall` VALUES ('53', '0', '0', '0', '0', '', '', '1779630691079');
INSERT INTO `clanhall` VALUES ('54', '0', '0', '0', '0', '', '', '1779630691093');
INSERT INTO `clanhall` VALUES ('55', '0', '0', '0', '0', '', '', '1779630691119');
INSERT INTO `clanhall` VALUES ('56', '0', '0', '0', '0', '', '', '1779630691141');
INSERT INTO `clanhall` VALUES ('57', '0', '0', '0', '0', '', '', '1779630691158');
INSERT INTO `clanhall` VALUES ('58', '0', '0', '0', '0', '', '', '1779630691176');
INSERT INTO `clanhall` VALUES ('59', '0', '0', '0', '0', '', '', '1779630691196');
INSERT INTO `clanhall` VALUES ('60', '0', '0', '0', '0', '', '', '1779630691221');
INSERT INTO `clanhall` VALUES ('61', '0', '0', '0', '0', '', '', '1779630691244');
INSERT INTO `clanhall` VALUES ('62', '0', '0', '0', '0', '', '', '1780239600263');
INSERT INTO `clanhall` VALUES ('63', '0', '0', '0', '0', '', '', '1780239600284');
INSERT INTO `clanhall` VALUES ('64', '0', '0', '0', '0', '', '', '1780239600323');

-- ----------------------------
-- Table structure for `clanhall_flagwar_attackers`
-- ----------------------------
DROP TABLE IF EXISTS `clanhall_flagwar_attackers`;
CREATE TABLE `clanhall_flagwar_attackers` (
  `clanhall_id` tinyint(2) unsigned NOT NULL DEFAULT 0,
  `flag` int(10) unsigned NOT NULL DEFAULT 0,
  `npc` int(10) unsigned NOT NULL DEFAULT 0,
  `clan_id` int(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`flag`),
  KEY `hall_id` (`clanhall_id`),
  KEY `clan_id` (`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clanhall_flagwar_attackers
-- ----------------------------

-- ----------------------------
-- Table structure for `clanhall_flagwar_members`
-- ----------------------------
DROP TABLE IF EXISTS `clanhall_flagwar_members`;
CREATE TABLE `clanhall_flagwar_members` (
  `clanhall_id` tinyint(2) unsigned NOT NULL DEFAULT 0,
  `clan_id` int(10) unsigned NOT NULL DEFAULT 0,
  `object_id` int(10) unsigned NOT NULL DEFAULT 0,
  KEY `clanhall_id` (`clanhall_id`),
  KEY `clan_id` (`clan_id`),
  KEY `object_id` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clanhall_flagwar_members
-- ----------------------------

-- ----------------------------
-- Table structure for `clanhall_flagwar_owner_npcs`
-- ----------------------------
DROP TABLE IF EXISTS `clanhall_flagwar_owner_npcs`;
CREATE TABLE `clanhall_flagwar_owner_npcs` (
  `clanhall_id` tinyint(2) unsigned NOT NULL DEFAULT 0,
  `npc_id` int(10) unsigned NOT NULL DEFAULT 0,
  `clan_id` int(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`clanhall_id`),
  KEY `npc_id` (`npc_id`),
  KEY `clan_id` (`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clanhall_flagwar_owner_npcs
-- ----------------------------

-- ----------------------------
-- Table structure for `clanhall_functions`
-- ----------------------------
DROP TABLE IF EXISTS `clanhall_functions`;
CREATE TABLE `clanhall_functions` (
  `hall_id` int(2) NOT NULL DEFAULT 0,
  `type` int(1) NOT NULL DEFAULT 0,
  `lvl` int(3) NOT NULL DEFAULT 0,
  `lease` int(10) NOT NULL DEFAULT 0,
  `rate` decimal(20,0) NOT NULL DEFAULT 0,
  `endTime` decimal(20,0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`hall_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clanhall_functions
-- ----------------------------
INSERT INTO `clanhall_functions` VALUES ('22', '1', '5000', '0', '0', '1779043287461');
INSERT INTO `clanhall_functions` VALUES ('22', '2', '30', '27500', '172800000', '1779198690170');
INSERT INTO `clanhall_functions` VALUES ('22', '4', '40', '70000', '259200000', '1779285090185');
INSERT INTO `clanhall_functions` VALUES ('22', '5', '2', '6000', '259200000', '1779285090198');
INSERT INTO `clanhall_functions` VALUES ('22', '7', '2', '2500', '604800000', '1779630690199');
INSERT INTO `clanhall_functions` VALUES ('22', '9', '5', '21000', '86400000', '1779236825175');
INSERT INTO `clanhall_functions` VALUES ('22', '11', '2', '4000', '259200000', '1779285090210');
INSERT INTO `clanhall_functions` VALUES ('22', '12', '3', '140000', '86400000', '1779236825948');

-- ----------------------------
-- Table structure for `clanhall_siege_attackers`
-- ----------------------------
DROP TABLE IF EXISTS `clanhall_siege_attackers`;
CREATE TABLE `clanhall_siege_attackers` (
  `clanhall_id` tinyint(2) NOT NULL DEFAULT 0,
  `attacker_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`clanhall_id`,`attacker_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clanhall_siege_attackers
-- ----------------------------

-- ----------------------------
-- Table structure for `clan_data`
-- ----------------------------
DROP TABLE IF EXISTS `clan_data`;
CREATE TABLE `clan_data` (
  `clan_id` int(11) NOT NULL DEFAULT 0,
  `clan_name` varchar(20) DEFAULT NULL,
  `clan_level` int(11) NOT NULL DEFAULT 0,
  `reputation_score` int(11) NOT NULL DEFAULT 0,
  `hasCastle` tinyint(4) NOT NULL DEFAULT 0,
  `ally_id` int(11) NOT NULL DEFAULT 0,
  `ally_name` varchar(20) DEFAULT NULL,
  `leader_id` int(11) NOT NULL DEFAULT 0,
  `new_leader_id` int(11) NOT NULL DEFAULT 0,
  `crest_id` int(11) NOT NULL DEFAULT 0,
  `crest_large_id` int(11) NOT NULL DEFAULT 0,
  `ally_crest_id` int(11) NOT NULL DEFAULT 0,
  `auction_bid_at` int(11) NOT NULL DEFAULT 0,
  `ally_penalty_expiry_time` bigint(20) NOT NULL DEFAULT 0,
  `ally_penalty_type` int(11) NOT NULL DEFAULT 0,
  `char_penalty_expiry_time` bigint(20) NOT NULL DEFAULT 0,
  `dissolving_expiry_time` bigint(20) NOT NULL DEFAULT 0,
  `enabled` tinyint(4) NOT NULL DEFAULT 0,
  `notice` text DEFAULT NULL,
  `introduction` text DEFAULT NULL,
  `graduates` text DEFAULT NULL,
  PRIMARY KEY (`clan_id`),
  KEY `leader_id` (`leader_id`),
  KEY `ally_id` (`ally_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clan_data
-- ----------------------------
INSERT INTO `clan_data` VALUES ('268493856', 'Instruction', '2', '0', '0', '0', null, '268494751', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', null, null, null);
INSERT INTO `clan_data` VALUES ('268494102', 'Staff', '0', '0', '0', '0', null, '268497182', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', null, null, null);
INSERT INTO `clan_data` VALUES ('268494332', 'Teste', '0', '0', '0', '0', null, '268494228', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', null, null, null);
INSERT INTO `clan_data` VALUES ('268510534', 'Crafter', '0', '0', '0', '0', null, '268509776', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', null, null, null);

-- ----------------------------
-- Table structure for `clan_privs`
-- ----------------------------
DROP TABLE IF EXISTS `clan_privs`;
CREATE TABLE `clan_privs` (
  `clan_id` int(11) NOT NULL DEFAULT 0,
  `ranking` int(11) NOT NULL DEFAULT 0,
  `privs` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`clan_id`,`ranking`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clan_privs
-- ----------------------------

-- ----------------------------
-- Table structure for `clan_skills`
-- ----------------------------
DROP TABLE IF EXISTS `clan_skills`;
CREATE TABLE `clan_skills` (
  `clan_id` int(11) NOT NULL DEFAULT 0,
  `skill_id` int(11) NOT NULL DEFAULT 0,
  `skill_level` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`clan_id`,`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clan_skills
-- ----------------------------
INSERT INTO `clan_skills` VALUES ('268493856', '370', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '371', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '372', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '373', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '374', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '375', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '376', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '377', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '378', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '379', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '380', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '381', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '382', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '383', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '384', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '385', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '386', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '387', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '388', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '389', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '390', '3');
INSERT INTO `clan_skills` VALUES ('268493856', '391', '1');
INSERT INTO `clan_skills` VALUES ('268494332', '370', '3');
INSERT INTO `clan_skills` VALUES ('268494332', '371', '3');
INSERT INTO `clan_skills` VALUES ('268494332', '372', '3');
INSERT INTO `clan_skills` VALUES ('268494332', '373', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '370', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '371', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '372', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '373', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '374', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '375', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '376', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '377', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '378', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '379', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '380', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '381', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '382', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '383', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '384', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '385', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '386', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '387', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '388', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '389', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '390', '3');
INSERT INTO `clan_skills` VALUES ('268510534', '391', '1');

-- ----------------------------
-- Table structure for `clan_subpledges`
-- ----------------------------
DROP TABLE IF EXISTS `clan_subpledges`;
CREATE TABLE `clan_subpledges` (
  `clan_id` int(11) NOT NULL DEFAULT 0,
  `sub_pledge_id` int(11) NOT NULL DEFAULT 0,
  `name` varchar(45) DEFAULT NULL,
  `leader_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`clan_id`,`sub_pledge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clan_subpledges
-- ----------------------------

-- ----------------------------
-- Table structure for `clan_wars`
-- ----------------------------
DROP TABLE IF EXISTS `clan_wars`;
CREATE TABLE `clan_wars` (
  `clan1` varchar(35) NOT NULL DEFAULT '',
  `clan2` varchar(35) NOT NULL DEFAULT '',
  `expiry_time` decimal(20,0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`clan1`,`clan2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of clan_wars
-- ----------------------------

-- ----------------------------
-- Table structure for `cursed_weapons`
-- ----------------------------
DROP TABLE IF EXISTS `cursed_weapons`;
CREATE TABLE `cursed_weapons` (
  `itemId` int(11) NOT NULL,
  `playerId` int(11) DEFAULT 0,
  `playerKarma` int(11) DEFAULT 0,
  `playerPkKills` int(11) DEFAULT 0,
  `nbKills` int(11) DEFAULT 0,
  `currentStage` int(11) DEFAULT 0,
  `numberBeforeNextStage` int(11) DEFAULT 0,
  `hungryTime` int(11) DEFAULT 0,
  `endTime` decimal(20,0) DEFAULT 0,
  PRIMARY KEY (`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of cursed_weapons
-- ----------------------------

-- ----------------------------
-- Table structure for `dungeon_cooldowns`
-- ----------------------------
DROP TABLE IF EXISTS `dungeon_cooldowns`;
CREATE TABLE `dungeon_cooldowns` (
  `dungeon_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `last_join` bigint(20) NOT NULL,
  `next_join` bigint(20) NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `stage` int(11) NOT NULL,
  PRIMARY KEY (`dungeon_id`,`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of dungeon_cooldowns
-- ----------------------------

-- ----------------------------
-- Table structure for `events_custom_data`
-- ----------------------------
DROP TABLE IF EXISTS `events_custom_data`;
CREATE TABLE `events_custom_data` (
  `event_name` varchar(35) NOT NULL,
  `status` decimal(1,0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`event_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of events_custom_data
-- ----------------------------
INSERT INTO `events_custom_data` VALUES ('Christmas', '0');
INSERT INTO `events_custom_data` VALUES ('CofferOfShadows', '0');
INSERT INTO `events_custom_data` VALUES ('HeavyMedal', '0');
INSERT INTO `events_custom_data` VALUES ('L2Day', '0');
INSERT INTO `events_custom_data` VALUES ('Squash', '0');

-- ----------------------------
-- Table structure for `fishing_championship`
-- ----------------------------
DROP TABLE IF EXISTS `fishing_championship`;
CREATE TABLE `fishing_championship` (
  `player_name` varchar(35) NOT NULL,
  `fish_length` double(10,3) NOT NULL,
  `rewarded` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of fishing_championship
-- ----------------------------

-- ----------------------------
-- Table structure for `games`
-- ----------------------------
DROP TABLE IF EXISTS `games`;
CREATE TABLE `games` (
  `id` int(11) NOT NULL DEFAULT 0,
  `idnr` int(11) NOT NULL DEFAULT 0,
  `number1` int(11) NOT NULL DEFAULT 0,
  `number2` int(11) NOT NULL DEFAULT 0,
  `prize` int(11) NOT NULL DEFAULT 0,
  `newprize` int(11) NOT NULL DEFAULT 0,
  `prize1` int(11) NOT NULL DEFAULT 0,
  `prize2` int(11) NOT NULL DEFAULT 0,
  `prize3` int(11) NOT NULL DEFAULT 0,
  `enddate` decimal(20,0) NOT NULL DEFAULT 0,
  `finished` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`,`idnr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of games
-- ----------------------------
INSERT INTO `games` VALUES ('1', '1', '29248', '0', '50000', '100000', '0', '0', '0', '1755381600720', '1');
INSERT INTO `games` VALUES ('1', '2', '2241', '1', '100000', '150000', '0', '0', '0', '1755986400720', '1');
INSERT INTO `games` VALUES ('1', '3', '38018', '0', '150000', '200000', '0', '0', '0', '1756591200720', '1');
INSERT INTO `games` VALUES ('1', '4', '1568', '5', '200000', '250000', '0', '0', '0', '1757196000720', '1');
INSERT INTO `games` VALUES ('1', '5', '588', '8', '250000', '300000', '0', '0', '0', '1757800800720', '1');
INSERT INTO `games` VALUES ('1', '6', '34088', '0', '300000', '350000', '0', '0', '0', '1758405600720', '1');
INSERT INTO `games` VALUES ('1', '7', '3587', '0', '350000', '400000', '0', '0', '0', '1759010400720', '1');
INSERT INTO `games` VALUES ('1', '8', '2380', '0', '400000', '450000', '0', '0', '0', '1759615200720', '1');
INSERT INTO `games` VALUES ('1', '9', '36868', '6', '450000', '500000', '0', '0', '0', '1760220000720', '1');
INSERT INTO `games` VALUES ('1', '10', '23552', '4', '500000', '550000', '0', '0', '0', '1760824800720', '1');
INSERT INTO `games` VALUES ('1', '11', '24588', '4', '550000', '600000', '0', '0', '0', '1761429600720', '1');
INSERT INTO `games` VALUES ('1', '12', '8512', '12', '600000', '650000', '0', '0', '0', '1762034400720', '1');
INSERT INTO `games` VALUES ('1', '13', '9252', '2', '650000', '700000', '0', '0', '0', '1762639200720', '1');
INSERT INTO `games` VALUES ('1', '14', '25856', '8', '700000', '750000', '0', '0', '0', '1763244000720', '1');
INSERT INTO `games` VALUES ('1', '15', '36865', '9', '750000', '800000', '0', '0', '0', '1763848800720', '1');
INSERT INTO `games` VALUES ('1', '16', '36996', '2', '800000', '850000', '0', '0', '0', '1764453600720', '1');
INSERT INTO `games` VALUES ('1', '17', '7188', '0', '850000', '900000', '0', '0', '0', '1765058400720', '1');
INSERT INTO `games` VALUES ('1', '18', '260', '7', '900000', '950000', '0', '0', '0', '1765663200720', '1');
INSERT INTO `games` VALUES ('1', '19', '24688', '0', '950000', '1000000', '0', '0', '0', '1766268000720', '1');
INSERT INTO `games` VALUES ('1', '20', '49312', '1', '1000000', '1050000', '0', '0', '0', '1766872800720', '1');
INSERT INTO `games` VALUES ('1', '21', '34984', '0', '1050000', '1100000', '0', '0', '0', '1767477600720', '1');
INSERT INTO `games` VALUES ('1', '22', '128', '15', '1100000', '1150000', '0', '0', '0', '1768082400720', '1');
INSERT INTO `games` VALUES ('1', '23', '35904', '2', '1150000', '1200000', '0', '0', '0', '1768687200720', '1');
INSERT INTO `games` VALUES ('1', '24', '27712', '0', '1200000', '1250000', '0', '0', '0', '1769292000720', '1');
INSERT INTO `games` VALUES ('1', '25', '41232', '4', '1250000', '1300000', '0', '0', '0', '1769896800720', '1');
INSERT INTO `games` VALUES ('1', '26', '18449', '8', '1300000', '1350000', '0', '0', '0', '1770501600720', '1');
INSERT INTO `games` VALUES ('1', '27', '13376', '8', '1350000', '1400000', '0', '0', '0', '1771106400707', '1');
INSERT INTO `games` VALUES ('1', '28', '32833', '10', '1400000', '1450000', '0', '0', '0', '1771711200707', '1');
INSERT INTO `games` VALUES ('1', '29', '8708', '5', '1450000', '1500000', '0', '0', '0', '1772316000707', '1');
INSERT INTO `games` VALUES ('1', '30', '4656', '2', '1500000', '1550000', '0', '0', '0', '1772920800707', '1');
INSERT INTO `games` VALUES ('1', '31', '12805', '0', '1550000', '1600000', '0', '0', '0', '1773525600707', '1');
INSERT INTO `games` VALUES ('1', '32', '796', '0', '1600000', '1650000', '0', '0', '0', '1774130400707', '1');
INSERT INTO `games` VALUES ('1', '33', '20568', '0', '1650000', '1700000', '0', '0', '0', '1774735200707', '1');
INSERT INTO `games` VALUES ('1', '34', '2200', '8', '1700000', '1750000', '0', '0', '0', '1775340000707', '1');
INSERT INTO `games` VALUES ('1', '35', '1208', '0', '1750000', '1800000', '0', '0', '0', '1775944800707', '1');
INSERT INTO `games` VALUES ('1', '36', '33284', '10', '1800000', '1850000', '0', '0', '0', '1776549600707', '1');
INSERT INTO `games` VALUES ('1', '37', '33546', '0', '1850000', '1900000', '0', '0', '0', '1777154400707', '1');
INSERT INTO `games` VALUES ('1', '38', '9484', '0', '1900000', '1950000', '0', '0', '0', '1777759200707', '1');
INSERT INTO `games` VALUES ('1', '39', '5', '14', '1950000', '2000000', '0', '0', '0', '1778364000707', '1');
INSERT INTO `games` VALUES ('1', '40', '0', '0', '2000000', '2000000', '0', '0', '0', '1779573600209', '0');

-- ----------------------------
-- Table structure for `gameservers`
-- ----------------------------
DROP TABLE IF EXISTS `gameservers`;
CREATE TABLE `gameservers` (
  `server_id` int(11) NOT NULL DEFAULT 0,
  `hexid` varchar(50) NOT NULL DEFAULT '',
  `host` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of gameservers
-- ----------------------------
INSERT INTO `gameservers` VALUES ('1', '5D423FA07A8ECC68BD4E171B30AB266C', '127.0.0.1');

-- ----------------------------
-- Table structure for `grandboss_list`
-- ----------------------------
DROP TABLE IF EXISTS `grandboss_list`;
CREATE TABLE `grandboss_list` (
  `player_id` decimal(11,0) NOT NULL,
  `zone` decimal(11,0) NOT NULL,
  PRIMARY KEY (`player_id`,`zone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of grandboss_list
-- ----------------------------

-- ----------------------------
-- Table structure for `heroes`
-- ----------------------------
DROP TABLE IF EXISTS `heroes`;
CREATE TABLE `heroes` (
  `char_id` decimal(11,0) NOT NULL DEFAULT 0,
  `class_id` decimal(3,0) NOT NULL DEFAULT 0,
  `count` decimal(3,0) NOT NULL DEFAULT 0,
  `played` decimal(1,0) NOT NULL DEFAULT 0,
  `active` tinyint(4) NOT NULL DEFAULT 0,
  `message` varchar(300) NOT NULL DEFAULT '',
  PRIMARY KEY (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of heroes
-- ----------------------------
INSERT INTO `heroes` VALUES ('268494406', '94', '1', '0', '0', '');
INSERT INTO `heroes` VALUES ('268497182', '92', '1', '0', '0', '');

-- ----------------------------
-- Table structure for `heroes_diary`
-- ----------------------------
DROP TABLE IF EXISTS `heroes_diary`;
CREATE TABLE `heroes_diary` (
  `char_id` int(10) unsigned NOT NULL,
  `time` bigint(13) unsigned NOT NULL DEFAULT 0,
  `action` tinyint(2) unsigned NOT NULL DEFAULT 0,
  `param` int(11) unsigned NOT NULL DEFAULT 0,
  KEY `char_id` (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of heroes_diary
-- ----------------------------
INSERT INTO `heroes_diary` VALUES ('268497182', '1760075901149', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760131252650', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760131363582', '1', '55023');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760132273420', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760132625745', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760133541389', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760133633043', '1', '55023');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760135192440', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760136152282', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760814620908', '1', '25319');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760815611875', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760819089718', '1', '55024');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760819246551', '1', '25319');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760820589428', '1', '25319');
INSERT INTO `heroes_diary` VALUES ('268497182', '1760821351254', '1', '25319');
INSERT INTO `heroes_diary` VALUES ('268497182', '1761278676906', '1', '25319');
INSERT INTO `heroes_diary` VALUES ('268497182', '1761456651945', '1', '25319');
INSERT INTO `heroes_diary` VALUES ('268497182', '1761543234905', '1', '25319');
INSERT INTO `heroes_diary` VALUES ('268497182', '1769048154422', '1', '25092');
INSERT INTO `heroes_diary` VALUES ('268497182', '1769048291636', '1', '29001');
INSERT INTO `heroes_diary` VALUES ('268497182', '1769626951153', '1', '25092');
INSERT INTO `heroes_diary` VALUES ('268497182', '1769697202538', '1', '29028');
INSERT INTO `heroes_diary` VALUES ('268497394', '1771052042283', '1', '25155');
INSERT INTO `heroes_diary` VALUES ('268497394', '1771056301027', '1', '25134');
INSERT INTO `heroes_diary` VALUES ('268497394', '1771062730240', '1', '25064');
INSERT INTO `heroes_diary` VALUES ('268497394', '1771083002247', '1', '25460');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771214346114', '1', '25155');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771215008032', '1', '25410');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771216932807', '1', '25415');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771217494567', '1', '25115');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771217847104', '1', '25487');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771218016624', '1', '25208');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771218095795', '1', '25214');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771218194339', '1', '25064');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771218285514', '1', '25490');
INSERT INTO `heroes_diary` VALUES ('268497182', '1771879045525', '1', '55030');
INSERT INTO `heroes_diary` VALUES ('268497182', '1772073470294', '1', '25309');
INSERT INTO `heroes_diary` VALUES ('268497182', '1773014204963', '1', '29047');
INSERT INTO `heroes_diary` VALUES ('268497182', '1773014351474', '1', '29065');
INSERT INTO `heroes_diary` VALUES ('268497182', '1773014445968', '1', '29068');
INSERT INTO `heroes_diary` VALUES ('268497182', '1773014758734', '1', '29022');

-- ----------------------------
-- Table structure for `hwid_bans`
-- ----------------------------
DROP TABLE IF EXISTS `hwid_bans`;
CREATE TABLE `hwid_bans` (
  `HWID` varchar(32) DEFAULT NULL,
  `HWIDSecond` varchar(32) DEFAULT NULL,
  `expiretime` int(11) NOT NULL DEFAULT 0,
  `comments` varchar(255) DEFAULT '',
  UNIQUE KEY `HWID` (`HWID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of hwid_bans
-- ----------------------------

-- ----------------------------
-- Table structure for `hwid_extra_boxes`
-- ----------------------------
DROP TABLE IF EXISTS `hwid_extra_boxes`;
CREATE TABLE `hwid_extra_boxes` (
  `hwid` varchar(64) NOT NULL,
  `extra_boxes` int(11) NOT NULL,
  PRIMARY KEY (`hwid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of hwid_extra_boxes
-- ----------------------------

-- ----------------------------
-- Table structure for `hwid_info`
-- ----------------------------
DROP TABLE IF EXISTS `hwid_info`;
CREATE TABLE `hwid_info` (
  `HWID` varchar(32) NOT NULL DEFAULT '',
  `Account` varchar(45) NOT NULL DEFAULT '',
  `PlayerID` int(10) unsigned NOT NULL DEFAULT 0,
  `LockType` enum('PLAYER_LOCK','ACCOUNT_LOCK','NONE') NOT NULL DEFAULT 'NONE',
  PRIMARY KEY (`HWID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of hwid_info
-- ----------------------------
INSERT INTO `hwid_info` VALUES ('7B35', 'dhousefe', '268494751', 'NONE');

-- ----------------------------
-- Table structure for `items`
-- ----------------------------
DROP TABLE IF EXISTS `items`;
CREATE TABLE `items` (
  `owner_id` int(11) DEFAULT NULL,
  `object_id` int(11) NOT NULL DEFAULT 0,
  `item_id` smallint(5) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL DEFAULT 0,
  `enchant_level` smallint(5) unsigned NOT NULL DEFAULT 0,
  `loc` varchar(10) DEFAULT NULL,
  `loc_data` int(11) DEFAULT NULL,
  `custom_type1` int(11) NOT NULL DEFAULT 0,
  `custom_type2` int(11) NOT NULL DEFAULT 0,
  `mana_left` int(11) NOT NULL DEFAULT -1,
  `time` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of items
-- ----------------------------
INSERT INTO `items` VALUES ('268436061', '268436062', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166350747');
INSERT INTO `items` VALUES ('268436061', '268436063', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166350750');
INSERT INTO `items` VALUES ('268436061', '268436064', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166350750');
INSERT INTO `items` VALUES ('268436061', '268436065', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166350751');
INSERT INTO `items` VALUES ('268436061', '268436066', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166350753');
INSERT INTO `items` VALUES ('268436061', '268436067', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166350753');
INSERT INTO `items` VALUES ('268436061', '268436068', '159', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166350753');
INSERT INTO `items` VALUES ('268436061', '268436069', '2493', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166350754');
INSERT INTO `items` VALUES ('268436061', '268436070', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166350755');
INSERT INTO `items` VALUES ('268436061', '268436071', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166350755');
INSERT INTO `items` VALUES ('268436061', '268436072', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166350756');
INSERT INTO `items` VALUES ('268436061', '268436073', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166350757');
INSERT INTO `items` VALUES ('268436074', '268436075', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166351275');
INSERT INTO `items` VALUES ('268436074', '268436076', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166351275');
INSERT INTO `items` VALUES ('268436074', '268436077', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166351275');
INSERT INTO `items` VALUES ('268436074', '268436078', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166351275');
INSERT INTO `items` VALUES ('268436074', '268436079', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166351276');
INSERT INTO `items` VALUES ('268436074', '268436080', '547', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166351276');
INSERT INTO `items` VALUES ('268436074', '268436081', '6608', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166351276');
INSERT INTO `items` VALUES ('268436074', '268436082', '6377', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166351276');
INSERT INTO `items` VALUES ('268436074', '268436083', '5770', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166351277');
INSERT INTO `items` VALUES ('268436074', '268436084', '2400', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166351277');
INSERT INTO `items` VALUES ('268436074', '268436085', '2405', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166351277');
INSERT INTO `items` VALUES ('268436074', '268436086', '5782', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166351277');
INSERT INTO `items` VALUES ('268436087', '268436088', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166353783');
INSERT INTO `items` VALUES ('268436087', '268436089', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166353784');
INSERT INTO `items` VALUES ('268436087', '268436090', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166353784');
INSERT INTO `items` VALUES ('268436087', '268436091', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166353784');
INSERT INTO `items` VALUES ('268436087', '268436092', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166353784');
INSERT INTO `items` VALUES ('268436087', '268436093', '2412', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166353784');
INSERT INTO `items` VALUES ('268436087', '268436094', '189', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166353785');
INSERT INTO `items` VALUES ('268436087', '268436095', '629', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166353785');
INSERT INTO `items` VALUES ('268436087', '268436096', '2450', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166353785');
INSERT INTO `items` VALUES ('268436087', '268436097', '437', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166353785');
INSERT INTO `items` VALUES ('268436087', '268436098', '470', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166353785');
INSERT INTO `items` VALUES ('268436087', '268436099', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166353786');
INSERT INTO `items` VALUES ('268436100', '268436101', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166354289');
INSERT INTO `items` VALUES ('268436100', '268436102', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166354289');
INSERT INTO `items` VALUES ('268436100', '268436103', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166354289');
INSERT INTO `items` VALUES ('268436100', '268436104', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166354289');
INSERT INTO `items` VALUES ('268436100', '268436105', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166354289');
INSERT INTO `items` VALUES ('268436100', '268436106', '2412', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166354290');
INSERT INTO `items` VALUES ('268436100', '268436107', '189', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166354290');
INSERT INTO `items` VALUES ('268436100', '268436108', '629', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166354290');
INSERT INTO `items` VALUES ('268436100', '268436109', '2450', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166354290');
INSERT INTO `items` VALUES ('268436100', '268436110', '437', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166354290');
INSERT INTO `items` VALUES ('268436100', '268436111', '470', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166354290');
INSERT INTO `items` VALUES ('268436100', '268436112', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166354291');
INSERT INTO `items` VALUES ('268436113', '268436114', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166356769');
INSERT INTO `items` VALUES ('268436113', '268436115', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166356769');
INSERT INTO `items` VALUES ('268436113', '268436116', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166356769');
INSERT INTO `items` VALUES ('268436113', '268436117', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166356769');
INSERT INTO `items` VALUES ('268436113', '268436118', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166356769');
INSERT INTO `items` VALUES ('268436113', '268436119', '2412', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166356770');
INSERT INTO `items` VALUES ('268436113', '268436120', '189', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166356770');
INSERT INTO `items` VALUES ('268436113', '268436121', '629', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166356770');
INSERT INTO `items` VALUES ('268436113', '268436122', '2450', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166356770');
INSERT INTO `items` VALUES ('268436113', '268436123', '437', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166356770');
INSERT INTO `items` VALUES ('268436113', '268436124', '470', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166356770');
INSERT INTO `items` VALUES ('268436113', '268436125', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166356770');
INSERT INTO `items` VALUES ('268436113', '268436126', '7837', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1779166356771');
INSERT INTO `items` VALUES ('268436127', '268436128', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166357159');
INSERT INTO `items` VALUES ('268436127', '268436129', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166357159');
INSERT INTO `items` VALUES ('268436127', '268436130', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166357159');
INSERT INTO `items` VALUES ('268436127', '268436131', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166357159');
INSERT INTO `items` VALUES ('268436127', '268436132', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166357159');
INSERT INTO `items` VALUES ('268436127', '268436133', '2412', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166357159');
INSERT INTO `items` VALUES ('268436127', '268436134', '189', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166357160');
INSERT INTO `items` VALUES ('268436127', '268436135', '629', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166357160');
INSERT INTO `items` VALUES ('268436127', '268436136', '2450', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166357160');
INSERT INTO `items` VALUES ('268436127', '268436137', '437', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166357160');
INSERT INTO `items` VALUES ('268436127', '268436138', '470', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166357160');
INSERT INTO `items` VALUES ('268436127', '268436139', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166357160');
INSERT INTO `items` VALUES ('268442483', '268447447', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166360666');
INSERT INTO `items` VALUES ('268442483', '268447448', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166360666');
INSERT INTO `items` VALUES ('268442483', '268447449', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166360666');
INSERT INTO `items` VALUES ('268442483', '268447450', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166360666');
INSERT INTO `items` VALUES ('268442483', '268447451', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166360666');
INSERT INTO `items` VALUES ('268442483', '268447452', '2412', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166360667');
INSERT INTO `items` VALUES ('268442483', '268447453', '189', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166360667');
INSERT INTO `items` VALUES ('268442483', '268447454', '629', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166360667');
INSERT INTO `items` VALUES ('268442483', '268447456', '2450', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166360667');
INSERT INTO `items` VALUES ('268442483', '268447457', '437', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166360667');
INSERT INTO `items` VALUES ('268442483', '268447458', '470', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166360667');
INSERT INTO `items` VALUES ('268442483', '268447459', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166360668');
INSERT INTO `items` VALUES ('268447460', '268449891', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166361137');
INSERT INTO `items` VALUES ('268447460', '268449892', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166361137');
INSERT INTO `items` VALUES ('268447460', '268449894', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166361137');
INSERT INTO `items` VALUES ('268447460', '268449895', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166361137');
INSERT INTO `items` VALUES ('268447460', '268449896', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166361137');
INSERT INTO `items` VALUES ('268447460', '268449897', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166361138');
INSERT INTO `items` VALUES ('268447460', '268449898', '2499', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166361138');
INSERT INTO `items` VALUES ('268447460', '268449899', '2493', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166361138');
INSERT INTO `items` VALUES ('268447460', '268449900', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166361138');
INSERT INTO `items` VALUES ('268447460', '268449902', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166361138');
INSERT INTO `items` VALUES ('268447460', '268449904', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166361138');
INSERT INTO `items` VALUES ('268447460', '268449905', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166361138');
INSERT INTO `items` VALUES ('268462570', '268464351', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464352', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464353', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464355', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464358', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464359', '2412', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464360', '189', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464361', '629', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464363', '2450', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464364', '437', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464365', '470', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166363465');
INSERT INTO `items` VALUES ('268462570', '268464368', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166363466');
INSERT INTO `items` VALUES ('268464372', '268466354', '850', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466356', '850', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466358', '913', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466359', '881', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466360', '881', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466361', '2412', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466363', '189', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466364', '629', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466366', '2450', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466367', '437', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166363900');
INSERT INTO `items` VALUES ('268464372', '268466373', '470', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779166363901');
INSERT INTO `items` VALUES ('268464372', '268466375', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166363903');
INSERT INTO `items` VALUES ('268476645', '268477334', '15007', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779166367163');
INSERT INTO `items` VALUES ('268476645', '268477335', '15001', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779166367163');
INSERT INTO `items` VALUES ('268476645', '268477336', '15003', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779166367164');
INSERT INTO `items` VALUES ('268476645', '268477337', '15009', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779166367164');
INSERT INTO `items` VALUES ('268476645', '268477339', '15005', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779166367164');
INSERT INTO `items` VALUES ('268476645', '268477340', '6382', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779166367164');
INSERT INTO `items` VALUES ('268476645', '268477341', '5706', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779166367164');
INSERT INTO `items` VALUES ('268476645', '268477342', '6380', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779166367165');
INSERT INTO `items` VALUES ('268476645', '268477343', '6379', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779166367165');
INSERT INTO `items` VALUES ('268476645', '268477344', '6381', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779166367165');
INSERT INTO `items` VALUES ('268476645', '268477345', '7694', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1779166367165');
INSERT INTO `items` VALUES ('268493856', '268493275', '1873', '23', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268493856', '268493276', '4041', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268631868', '268493283', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268493285', '268493286', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1770936756242');
INSERT INTO `items` VALUES ('268493285', '268493287', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1770936756241');
INSERT INTO `items` VALUES ('268493285', '268493288', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936756238');
INSERT INTO `items` VALUES ('268493285', '268493289', '2369', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1770936756239');
INSERT INTO `items` VALUES ('268493285', '268493290', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936756238');
INSERT INTO `items` VALUES ('268493285', '268493292', '57', '86500043', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936756238');
INSERT INTO `items` VALUES ('268497182', '268493294', '4041', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493578', '268493295', '159', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005317982');
INSERT INTO `items` VALUES ('268493578', '268493296', '882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005324099');
INSERT INTO `items` VALUES ('268497182', '268493297', '1832', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493578', '268493298', '882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005325147');
INSERT INTO `items` VALUES ('268493578', '268493299', '851', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005327006');
INSERT INTO `items` VALUES ('268509776', '268493300', '4043', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268493578', '268493301', '851', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005327815');
INSERT INTO `items` VALUES ('268493302', '268493303', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1770524904374');
INSERT INTO `items` VALUES ('268493302', '268493304', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1770524904371');
INSERT INTO `items` VALUES ('268493302', '268493305', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770524904369');
INSERT INTO `items` VALUES ('268493302', '268493306', '2369', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1770524904370');
INSERT INTO `items` VALUES ('268493302', '268493307', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770524904369');
INSERT INTO `items` VALUES ('268493578', '268493308', '914', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005329578');
INSERT INTO `items` VALUES ('268493578', '268493310', '2493', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005333847');
INSERT INTO `items` VALUES ('268493302', '268493311', '57', '7035', '0', 'INVENTORY', '0', '0', '0', '-1', '1770524952279');
INSERT INTO `items` VALUES ('268493302', '268493312', '6353', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770524974247');
INSERT INTO `items` VALUES ('268493309', '268493314', '57', '137333050', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493578', '268493315', '2449', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005337104');
INSERT INTO `items` VALUES ('268493578', '268493317', '352', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005340725');
INSERT INTO `items` VALUES ('268493578', '268493318', '2411', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005342666');
INSERT INTO `items` VALUES ('268493896', '268493319', '1875', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268497182', '268493321', '1866', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493285', '268493322', '4037', '950', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936798836');
INSERT INTO `items` VALUES ('268497182', '268493323', '1345', '41536', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779162433868');
INSERT INTO `items` VALUES ('268497182', '268493324', '1467', '11312', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493285', '268493329', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936844817');
INSERT INTO `items` VALUES ('268493285', '268493330', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936846356');
INSERT INTO `items` VALUES ('268493896', '268493333', '1989', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268498467', '268493335', '2011', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268494048', '268493336', '1865', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055086037');
INSERT INTO `items` VALUES ('268497182', '268493337', '5589', '19745', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268494751', '268493340', '1882', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494751', '268493341', '1872', '60', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268493856', '268493344', '6346', '2', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491338');
INSERT INTO `items` VALUES ('268494751', '268493346', '5496', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494751', '268493347', '5509', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268493856', '268493348', '1872', '50', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268494751', '268493349', '5508', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268493856', '268493351', '1881', '2', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268494751', '268493353', '1867', '61', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691649');
INSERT INTO `items` VALUES ('268509776', '268493354', '2161', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268493285', '268493355', '5789', '398', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936756238');
INSERT INTO `items` VALUES ('268494324', '268493365', '1870', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1771555076332');
INSERT INTO `items` VALUES ('268493856', '268493368', '1882', '6', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268493856', '268493371', '1889', '4', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268493896', '268493376', '2917', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031690266');
INSERT INTO `items` VALUES ('268493309', '268493382', '1889', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268498467', '268493392', '1934', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167810');
INSERT INTO `items` VALUES ('268498467', '268493393', '1931', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167810');
INSERT INTO `items` VALUES ('268493856', '268493394', '6344', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491338');
INSERT INTO `items` VALUES ('268493856', '268493395', '6342', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491338');
INSERT INTO `items` VALUES ('268494228', '268493435', '5296', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654097');
INSERT INTO `items` VALUES ('268494751', '268493443', '1889', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691649');
INSERT INTO `items` VALUES ('268497182', '268493461', '15003', '1', '6', 'PAPERDOLL', '3', '0', '0', '-1', '1779162433861');
INSERT INTO `items` VALUES ('268494228', '268493463', '3958', '29', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268493464', '3959', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268497182', '268493466', '6393', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493467', '268493468', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1769384151062');
INSERT INTO `items` VALUES ('268493467', '268493469', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1769384151061');
INSERT INTO `items` VALUES ('268493467', '268493470', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151052');
INSERT INTO `items` VALUES ('268493467', '268493471', '2369', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1769384151058');
INSERT INTO `items` VALUES ('268493467', '268493472', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151052');
INSERT INTO `items` VALUES ('268493467', '268493473', '57', '8184', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151052');
INSERT INTO `items` VALUES ('268497182', '268493475', '6392', '30435', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268497182', '268493476', '1867', '31', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268497182', '268493477', '6391', '32871', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268497182', '268493484', '57', '1361890133', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493309', '268493488', '6360', '368', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268494228', '268493506', '1884', '5', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494228', '268493507', '1878', '5', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494228', '268493508', '2152', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494228', '268493514', '1341', '463', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494356', '268493516', '1872', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268493517', '1867', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494667', '268493528', '1464', '1000', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423795642');
INSERT INTO `items` VALUES ('268497927', '268493532', '4037', '449', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936989032');
INSERT INTO `items` VALUES ('268497927', '268493534', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770937028852');
INSERT INTO `items` VALUES ('268497927', '268493535', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1770937030242');
INSERT INTO `items` VALUES ('268494228', '268493542', '1882', '4', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494228', '268493544', '1871', '5', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268493578', '268493545', '2378', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005403145');
INSERT INTO `items` VALUES ('268493578', '268493546', '1864', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005439838');
INSERT INTO `items` VALUES ('268493578', '268493547', '1878', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005439839');
INSERT INTO `items` VALUES ('268493578', '268493549', '1869', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005454244');
INSERT INTO `items` VALUES ('268494751', '268493558', '8954', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691649');
INSERT INTO `items` VALUES ('268493309', '268493563', '1981', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268493564', '2956', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493578', '268493568', '728', '1893', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005557644');
INSERT INTO `items` VALUES ('268494228', '268493571', '2147', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268493578', '268493572', '1539', '1984', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005561796');
INSERT INTO `items` VALUES ('268493856', '268493579', '8598', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268494048', '268493582', '4037', '37', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004003');
INSERT INTO `items` VALUES ('268494048', '268493583', '3875', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494048', '268493585', '3878', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494228', '268493588', '1864', '6', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494228', '268493591', '3888', '115', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268493578', '268493594', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089510');
INSERT INTO `items` VALUES ('268494228', '268493595', '3877', '70', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268493578', '268493596', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089510');
INSERT INTO `items` VALUES ('268493578', '268493597', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089510');
INSERT INTO `items` VALUES ('268497182', '268493598', '5625', '1', '45', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493578', '268493601', '15017', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089502');
INSERT INTO `items` VALUES ('268493578', '268493602', '15023', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089502');
INSERT INTO `items` VALUES ('268493578', '268493603', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089502');
INSERT INTO `items` VALUES ('268493578', '268493604', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089496');
INSERT INTO `items` VALUES ('268493578', '268493606', '57', '46503272', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089496');
INSERT INTO `items` VALUES ('268493896', '268493609', '1986', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031777863');
INSERT INTO `items` VALUES ('268493605', '268493611', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493605', '268493612', '4037', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493605', '268493615', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493896', '268493617', '6669', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031865107');
INSERT INTO `items` VALUES ('268493605', '268493631', '3948', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493578', '268493637', '1067', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089496');
INSERT INTO `items` VALUES ('268493578', '268493638', '5789', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089496');
INSERT INTO `items` VALUES ('268493605', '268493642', '1463', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493578', '268493643', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089496');
INSERT INTO `items` VALUES ('268493578', '268493644', '7694', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1773005089527');
INSERT INTO `items` VALUES ('268493578', '268493646', '6360', '521', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005638286');
INSERT INTO `items` VALUES ('268493578', '268493647', '6362', '130', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005638286');
INSERT INTO `items` VALUES ('268493578', '268493654', '1463', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005089496');
INSERT INTO `items` VALUES ('268497182', '268493659', '15001', '1', '6', 'PAPERDOLL', '2', '0', '0', '-1', '1779162433858');
INSERT INTO `items` VALUES ('268494406', '268493661', '1895', '37', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268497182', '268493663', '15007', '1', '6', 'PAPERDOLL', '1', '0', '0', '-1', '1779162433857');
INSERT INTO `items` VALUES ('268497182', '268493665', '15005', '1', '6', 'PAPERDOLL', '5', '0', '0', '-1', '1779162433864');
INSERT INTO `items` VALUES ('268493309', '268493668', '3031', '395', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268493687', '9265', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268493689', '8562', '1', '1', 'PAPERDOLL', '16', '0', '0', '-1', '1773010898635');
INSERT INTO `items` VALUES ('268497182', '268493694', '5556', '50', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433855');
INSERT INTO `items` VALUES ('268493707', '268493708', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881176527');
INSERT INTO `items` VALUES ('268493707', '268493713', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881176525');
INSERT INTO `items` VALUES ('268497182', '268493716', '7577', '1', '45', 'PAPERDOLL', '7', '0', '0', '-1', '1779162433866');
INSERT INTO `items` VALUES ('268497394', '268493718', '855', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777433628160');
INSERT INTO `items` VALUES ('268497182', '268493721', '15017', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433855');
INSERT INTO `items` VALUES ('268493707', '268493722', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881176524');
INSERT INTO `items` VALUES ('268497182', '268493726', '9270', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433855');
INSERT INTO `items` VALUES ('268493707', '268493734', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881176521');
INSERT INTO `items` VALUES ('268493707', '268493735', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881176521');
INSERT INTO `items` VALUES ('268494228', '268493744', '8661', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1777087654110');
INSERT INTO `items` VALUES ('268497182', '268493747', '1880', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433854');
INSERT INTO `items` VALUES ('268494228', '268493768', '1865', '3', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494228', '268493772', '1866', '5', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494228', '268493780', '1867', '2', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268494048', '268493793', '5559', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268495632', '268493794', '18', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768716813243');
INSERT INTO `items` VALUES ('268509776', '268493797', '1880', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268493798', '1889', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268493799', '1864', '45', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268494751', '268493804', '4042', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268493809', '268493810', '425', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1768970200345');
INSERT INTO `items` VALUES ('268493809', '268493811', '461', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1768970200348');
INSERT INTO `items` VALUES ('268493809', '268493812', '6', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1768970200343');
INSERT INTO `items` VALUES ('268493809', '268493813', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768970200342');
INSERT INTO `items` VALUES ('268497182', '268493815', '5706', '1', '45', 'INVENTORY', '0', '0', '0', '-1', '1779162433854');
INSERT INTO `items` VALUES ('268497182', '268493821', '3031', '472', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268497182', '268493828', '1539', '215', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493309', '268493830', '84', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1773010898626');
INSERT INTO `items` VALUES ('268494751', '268493831', '1873', '34', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268494751', '268493832', '6361', '51410', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268494751', '268493834', '1868', '176', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268494751', '268493835', '1876', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494751', '268493836', '1344', '2966', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494324', '268493837', '1419', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1771555433513');
INSERT INTO `items` VALUES ('268493467', '268493839', '21', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151053');
INSERT INTO `items` VALUES ('268493467', '268493840', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151054');
INSERT INTO `items` VALUES ('268497182', '268493842', '1882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268494667', '268493843', '1871', '96', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430342');
INSERT INTO `items` VALUES ('268497182', '268493844', '2971', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268497182', '268493845', '1881', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268497182', '268493846', '1341', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493309', '268493848', '6393', '648', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268494184', '268493849', '2174', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382103');
INSERT INTO `items` VALUES ('268493809', '268493858', '57', '818', '0', 'INVENTORY', '0', '0', '0', '-1', '1768970200342');
INSERT INTO `items` VALUES ('268493309', '268493859', '439', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1773010898630');
INSERT INTO `items` VALUES ('268493896', '268493860', '4057', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032052671');
INSERT INTO `items` VALUES ('268493309', '268493861', '471', '1', '1', 'PAPERDOLL', '12', '0', '0', '-1', '1773010898632');
INSERT INTO `items` VALUES ('268494048', '268493863', '3887', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268493309', '268493865', '2454', '1', '1', 'PAPERDOLL', '10', '0', '0', '-1', '1773010898629');
INSERT INTO `items` VALUES ('268493309', '268493866', '6666', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493605', '268493867', '57', '71998506', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493309', '268493868', '2497', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1773010898628');
INSERT INTO `items` VALUES ('268494751', '268493869', '1895', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268497182', '268493870', '6381', '1', '15', 'PAPERDOLL', '13', '0', '0', '-1', '1779162433875');
INSERT INTO `items` VALUES ('268493876', '268493877', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268497182', '268493879', '4037', '85', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493876', '268493880', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569341');
INSERT INTO `items` VALUES ('268493876', '268493881', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569340');
INSERT INTO `items` VALUES ('268493876', '268493882', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569339');
INSERT INTO `items` VALUES ('268493876', '268493883', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268493876', '268493884', '57', '28015283', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268493876', '268493887', '1026', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268494751', '268493890', '1459', '150', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268493467', '268493894', '1067', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151054');
INSERT INTO `items` VALUES ('268493467', '268493895', '5789', '37', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151054');
INSERT INTO `items` VALUES ('268493896', '268493901', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268497182', '268493903', '6651', '180', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493856', '268493904', '4044', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493467', '268493906', '462', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151055');
INSERT INTO `items` VALUES ('268493896', '268493907', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268494751', '268493908', '8633', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268493876', '268493909', '1069', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268494228', '268493915', '6393', '1481', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273561');
INSERT INTO `items` VALUES ('268494228', '268493916', '3878', '96', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268493917', '6391', '1533', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268493918', '5558', '551', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268493919', '3879', '110', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268493923', '6392', '1884', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273560');
INSERT INTO `items` VALUES ('268494228', '268493924', '6362', '533', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268493925', '3884', '90', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654097');
INSERT INTO `items` VALUES ('268494228', '268493927', '3887', '85', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654097');
INSERT INTO `items` VALUES ('268494228', '268493928', '1866', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088481906');
INSERT INTO `items` VALUES ('268494228', '268493929', '1871', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088481906');
INSERT INTO `items` VALUES ('268497182', '268493930', '2375', '1', '15', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268494751', '268493935', '8597', '91', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268493578', '268493936', '1868', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005799886');
INSERT INTO `items` VALUES ('268493578', '268493937', '6037', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005799886');
INSERT INTO `items` VALUES ('268494228', '268493938', '1876', '2', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777087654113');
INSERT INTO `items` VALUES ('268493896', '268493946', '97', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1771032242041');
INSERT INTO `items` VALUES ('268493605', '268493949', '5789', '400', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268497182', '268493950', '15018', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268497182', '268493953', '15021', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433856');
INSERT INTO `items` VALUES ('268493913', '268493954', '3948', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121637');
INSERT INTO `items` VALUES ('268493896', '268493963', '57', '73881202', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268493913', '268493965', '57', '96311171', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268497182', '268493968', '6380', '1', '15', 'PAPERDOLL', '10', '0', '0', '-1', '1779162433873');
INSERT INTO `items` VALUES ('268493605', '268493970', '1792', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493605', '268493971', '1868', '48', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493142', '268493972', '4037', '35', '0', 'INVENTORY', '0', '0', '0', '-1', '1756151288916');
INSERT INTO `items` VALUES ('268493605', '268493973', '1873', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268493974', '1870', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493896', '268493975', '1498', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268493605', '268493977', '1803', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493980', '268493982', '425', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1766978332492');
INSERT INTO `items` VALUES ('268493980', '268493986', '461', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1766978332494');
INSERT INTO `items` VALUES ('268493980', '268493987', '6', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1766978332491');
INSERT INTO `items` VALUES ('268493980', '268493988', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1766978332489');
INSERT INTO `items` VALUES ('268493980', '268493989', '57', '344', '0', 'INVENTORY', '0', '0', '0', '-1', '1766978366066');
INSERT INTO `items` VALUES ('268497182', '268493995', '6400', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268494048', '268493996', '1869', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494048', '268493997', '1864', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494048', '268493998', '1871', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494048', '268493999', '1866', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494228', '268494001', '5559', '35', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268497182', '268494008', '5557', '4519', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268494324', '268494013', '1867', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771555525076');
INSERT INTO `items` VALUES ('268494324', '268494014', '1872', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771555525076');
INSERT INTO `items` VALUES ('268494228', '268494030', '6579', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1777087654106');
INSERT INTO `items` VALUES ('268493578', '268494035', '2548', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005925687');
INSERT INTO `items` VALUES ('268493578', '268494036', '5589', '8997', '0', 'INVENTORY', '0', '0', '0', '-1', '1773005969563');
INSERT INTO `items` VALUES ('268493309', '268494039', '64', '1', '1', 'PAPERDOLL', '13', '0', '0', '-1', '1773010898634');
INSERT INTO `items` VALUES ('268497182', '268494040', '1895', '163', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268494406', '268494042', '3031', '5601', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100491');
INSERT INTO `items` VALUES ('268493309', '268494043', '852', '1', '1', 'PAPERDOLL', '2', '0', '0', '-1', '1773010898620');
INSERT INTO `items` VALUES ('268493605', '268494044', '1865', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268494228', '268494046', '3881', '114', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268494047', '3886', '83', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268494049', '3875', '93', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268494050', '5557', '177', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268494051', '3882', '92', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494048', '268494052', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004010');
INSERT INTO `items` VALUES ('268494048', '268494053', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004009');
INSERT INTO `items` VALUES ('268494048', '268494054', '2368', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004005');
INSERT INTO `items` VALUES ('268494048', '268494055', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004004');
INSERT INTO `items` VALUES ('268494048', '268494056', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004003');
INSERT INTO `items` VALUES ('268494048', '268494057', '57', '890874568', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268497182', '268494058', '6379', '1', '15', 'PAPERDOLL', '11', '0', '0', '-1', '1779162433874');
INSERT INTO `items` VALUES ('268497182', '268494059', '6382', '1', '15', 'PAPERDOLL', '6', '0', '0', '-1', '1779162433865');
INSERT INTO `items` VALUES ('268493309', '268494061', '852', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1773010898618');
INSERT INTO `items` VALUES ('268494228', '268494062', '5560', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494406', '268494064', '9270', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493309', '268494065', '916', '1', '1', 'PAPERDOLL', '3', '0', '0', '-1', '1773010898621');
INSERT INTO `items` VALUES ('268493309', '268494066', '886', '1', '1', 'PAPERDOLL', '4', '0', '0', '-1', '1773010898623');
INSERT INTO `items` VALUES ('268493309', '268494067', '886', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1773010898624');
INSERT INTO `items` VALUES ('268493309', '268494077', '4612', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268494078', '4607', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268494406', '268494082', '728', '1064', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494751', '268494085', '4043', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494406', '268494088', '6651', '60', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100491');
INSERT INTO `items` VALUES ('268493309', '268494090', '3949', '50008', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898616');
INSERT INTO `items` VALUES ('268509776', '268494095', '1908', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268494096', '1909', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268493856', '268494097', '1894', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268509776', '268494099', '2217', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268494103', '1905', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268494667', '268494105', '6668', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777424362508');
INSERT INTO `items` VALUES ('268494751', '268494111', '3031', '181', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494048', '268494114', '3877', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268493809', '268494120', '1068', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768970200342');
INSERT INTO `items` VALUES ('268494406', '268494121', '5643', '1', '6', 'PAPERDOLL', '7', '0', '0', '-1', '1770950100500');
INSERT INTO `items` VALUES ('268494406', '268494123', '3951', '48647', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100491');
INSERT INTO `items` VALUES ('268494406', '268494124', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493809', '268494126', '5790', '67', '0', 'INVENTORY', '0', '0', '0', '-1', '1768970200342');
INSERT INTO `items` VALUES ('268494048', '268494132', '1882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494048', '268494136', '3888', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494048', '268494137', '5557', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268493809', '268494140', '17', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1768970200342');
INSERT INTO `items` VALUES ('268493809', '268494141', '1868', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768970200342');
INSERT INTO `items` VALUES ('268494228', '268494143', '2454', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1777087654107');
INSERT INTO `items` VALUES ('268494228', '268494145', '1149', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1777087654105');
INSERT INTO `items` VALUES ('268494228', '268494161', '6400', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494228', '268494167', '924', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1777087654102');
INSERT INTO `items` VALUES ('268494228', '268494168', '893', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1777087654103');
INSERT INTO `items` VALUES ('268494228', '268494169', '893', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1777087654104');
INSERT INTO `items` VALUES ('268494228', '268494170', '862', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1777087654099');
INSERT INTO `items` VALUES ('268494228', '268494171', '862', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1777087654101');
INSERT INTO `items` VALUES ('268494228', '268494172', '6383', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1777087654108');
INSERT INTO `items` VALUES ('268494228', '268494173', '6385', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1777087654109');
INSERT INTO `items` VALUES ('268497182', '268494175', '9266', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268494228', '268494176', '6377', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1777087654107');
INSERT INTO `items` VALUES ('268494228', '268494178', '3952', '47545', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494228', '268494179', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268493896', '268494180', '1465', '47536', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032398682');
INSERT INTO `items` VALUES ('268493809', '268494181', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768970200342');
INSERT INTO `items` VALUES ('268493605', '268494182', '1866', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268494183', '1871', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268494184', '268494185', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1771025382124');
INSERT INTO `items` VALUES ('268494184', '268494186', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1771025382123');
INSERT INTO `items` VALUES ('268494184', '268494187', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382114');
INSERT INTO `items` VALUES ('268494184', '268494188', '2370', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1771025382121');
INSERT INTO `items` VALUES ('268494184', '268494189', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382114');
INSERT INTO `items` VALUES ('268494406', '268494190', '9581', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268497182', '268494220', '4040', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268494048', '268494238', '70', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1756055004018');
INSERT INTO `items` VALUES ('268494048', '268494239', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1756055004017');
INSERT INTO `items` VALUES ('268494048', '268494240', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1756055004020');
INSERT INTO `items` VALUES ('268494048', '268494241', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1756055004021');
INSERT INTO `items` VALUES ('268494048', '268494242', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1756055004019');
INSERT INTO `items` VALUES ('268494048', '268494243', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1756055004022');
INSERT INTO `items` VALUES ('268494048', '268494244', '2493', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004003');
INSERT INTO `items` VALUES ('268494048', '268494245', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1756055004016');
INSERT INTO `items` VALUES ('268494048', '268494246', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1756055004016');
INSERT INTO `items` VALUES ('268494048', '268494247', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1756055004015');
INSERT INTO `items` VALUES ('268494048', '268494248', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1756055004013');
INSERT INTO `items` VALUES ('268494048', '268494249', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1756055004015');
INSERT INTO `items` VALUES ('268494048', '268494250', '1463', '134', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004011');
INSERT INTO `items` VALUES ('268494048', '268494251', '728', '668', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494252', '1539', '484', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494253', '3031', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494254', '1785', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494255', '1459', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004001');
INSERT INTO `items` VALUES ('268493980', '268494261', '5790', '189', '0', 'INVENTORY', '0', '0', '0', '-1', '1766978439783');
INSERT INTO `items` VALUES ('268493896', '268494262', '115', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1771031469680');
INSERT INTO `items` VALUES ('268493896', '268494264', '877', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1771031469687');
INSERT INTO `items` VALUES ('268493896', '268494267', '3470', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268493896', '268494268', '7694', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1771031469700');
INSERT INTO `items` VALUES ('268497182', '268494276', '15009', '1', '6', 'PAPERDOLL', '4', '0', '0', '-1', '1779162433862');
INSERT INTO `items` VALUES ('268494751', '268494284', '5549', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494048', '268494294', '1458', '474', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004001');
INSERT INTO `items` VALUES ('268493605', '268494302', '1831', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268494228', '268494305', '5556', '176', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494228', '268494306', '3876', '104', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494184', '268494313', '57', '247337116', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382114');
INSERT INTO `items` VALUES ('268494184', '268494322', '1498', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382114');
INSERT INTO `items` VALUES ('268494324', '268494329', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268494334', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268494335', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268494336', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494048', '268494338', '3876', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004001');
INSERT INTO `items` VALUES ('268494324', '268494339', '15017', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268494342', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494048', '268494345', '3881', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004001');
INSERT INTO `items` VALUES ('268494324', '268494346', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268494347', '57', '46491309', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494356', '268494359', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606448');
INSERT INTO `items` VALUES ('268494356', '268494360', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606447');
INSERT INTO `items` VALUES ('268494356', '268494361', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494324', '268494367', '1463', '50004', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494406', '268494371', '5556', '41', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494406', '268494381', '882', '1', '6', 'PAPERDOLL', '4', '0', '0', '-1', '1770950100497');
INSERT INTO `items` VALUES ('268494406', '268494386', '882', '1', '6', 'PAPERDOLL', '5', '0', '0', '-1', '1770950100498');
INSERT INTO `items` VALUES ('268494406', '268494388', '851', '1', '6', 'PAPERDOLL', '2', '0', '0', '-1', '1770950100493');
INSERT INTO `items` VALUES ('268494406', '268494389', '851', '1', '6', 'PAPERDOLL', '1', '0', '0', '-1', '1770950100492');
INSERT INTO `items` VALUES ('268494406', '268494393', '914', '1', '6', 'PAPERDOLL', '3', '0', '0', '-1', '1770950100495');
INSERT INTO `items` VALUES ('268493605', '268494403', '1867', '34', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268494406', '268494405', '4037', '950', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100491');
INSERT INTO `items` VALUES ('268493605', '268494409', '735', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493605', '268494410', '1872', '35', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268494324', '268494411', '5575', '116869', '0', 'INVENTORY', '0', '0', '0', '-1', '1771555703910');
INSERT INTO `items` VALUES ('268494751', '268494417', '5707', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268509776', '268494418', '1874', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697229662');
INSERT INTO `items` VALUES ('268509776', '268494420', '1915', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697237271');
INSERT INTO `items` VALUES ('268494048', '268494421', '3879', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494406', '268494438', '1832', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100491');
INSERT INTO `items` VALUES ('268509776', '268494442', '2170', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697285670');
INSERT INTO `items` VALUES ('268494324', '268494447', '1067', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268494448', '5789', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268497182', '268494450', '3470', '50', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268494406', '268494451', '57', '755503285', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100491');
INSERT INTO `items` VALUES ('268494048', '268494454', '3882', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494455', '1876', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494456', '1341', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494458', '5558', '18', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494459', '6392', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494460', '3884', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494461', '6391', '40', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268493856', '268494472', '57', '85568000', '0', 'CLANWH', '0', '0', '0', '-1', '1779150403096');
INSERT INTO `items` VALUES ('268493309', '268494477', '851', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898617');
INSERT INTO `items` VALUES ('268493309', '268494478', '914', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898616');
INSERT INTO `items` VALUES ('268494048', '268494480', '5556', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494481', '6393', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004002');
INSERT INTO `items` VALUES ('268494048', '268494482', '3886', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1756055004001');
INSERT INTO `items` VALUES ('268494751', '268494485', '6037', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494228', '268494502', '4037', '547', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273556');
INSERT INTO `items` VALUES ('268493578', '268494516', '1832', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006174740');
INSERT INTO `items` VALUES ('268493578', '268494517', '1871', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006174740');
INSERT INTO `items` VALUES ('268493578', '268494518', '1872', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006184577');
INSERT INTO `items` VALUES ('268493578', '268494519', '1867', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006184577');
INSERT INTO `items` VALUES ('268494356', '268494535', '57', '71836389', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268509776', '268494541', '1832', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697436111');
INSERT INTO `items` VALUES ('268493896', '268494569', '6392', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032791356');
INSERT INTO `items` VALUES ('268493896', '268494575', '1895', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032811873');
INSERT INTO `items` VALUES ('268493896', '268494576', '4043', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032811873');
INSERT INTO `items` VALUES ('268493896', '268494578', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032815418');
INSERT INTO `items` VALUES ('268494667', '268494584', '1866', '82', '0', 'INVENTORY', '0', '0', '0', '-1', '1777434077437');
INSERT INTO `items` VALUES ('268494667', '268494585', '1871', '107', '0', 'INVENTORY', '0', '0', '0', '-1', '1777424686009');
INSERT INTO `items` VALUES ('268509776', '268494612', '1419', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697510932');
INSERT INTO `items` VALUES ('268494751', '268494637', '7666', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268494751', '268494641', '7646', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268493605', '268494642', '1869', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493605', '268494643', '1864', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268494751', '268494644', '7648', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691650');
INSERT INTO `items` VALUES ('268497182', '268494648', '1344', '2440', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268497182', '268494652', '4042', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268493467', '268494654', '17', '208', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151057');
INSERT INTO `items` VALUES ('268497182', '268494663', '7917', '22', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268494667', '268494668', '425', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430343');
INSERT INTO `items` VALUES ('268494751', '268494669', '1458', '144', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268494184', '268494670', '3031', '457', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382114');
INSERT INTO `items` VALUES ('268494184', '268494671', '1463', '1997', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382114');
INSERT INTO `items` VALUES ('268494184', '268494672', '187', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382116');
INSERT INTO `items` VALUES ('268494184', '268494673', '2411', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382120');
INSERT INTO `items` VALUES ('268494184', '268494674', '352', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382119');
INSERT INTO `items` VALUES ('268494184', '268494675', '2378', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382118');
INSERT INTO `items` VALUES ('268494184', '268494676', '2449', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382118');
INSERT INTO `items` VALUES ('268494184', '268494677', '2425', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382112');
INSERT INTO `items` VALUES ('268494184', '268494678', '2493', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382111');
INSERT INTO `items` VALUES ('268494184', '268494679', '882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382110');
INSERT INTO `items` VALUES ('268494184', '268494680', '882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382106');
INSERT INTO `items` VALUES ('268494184', '268494681', '851', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382107');
INSERT INTO `items` VALUES ('268494184', '268494682', '851', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382108');
INSERT INTO `items` VALUES ('268494184', '268494683', '914', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268497182', '268494684', '15022', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268494685', '7694', '1', '6', 'PAPERDOLL', '15', '0', '0', '-1', '1779162433876');
INSERT INTO `items` VALUES ('268494667', '268494686', '461', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430345');
INSERT INTO `items` VALUES ('268494667', '268494687', '6', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430346');
INSERT INTO `items` VALUES ('268494667', '268494688', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430322');
INSERT INTO `items` VALUES ('268494667', '268494700', '57', '273984144', '0', 'INVENTORY', '0', '0', '0', '-1', '1777434077438');
INSERT INTO `items` VALUES ('268494667', '268494703', '1068', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430322');
INSERT INTO `items` VALUES ('268494667', '268494704', '5790', '99', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430346');
INSERT INTO `items` VALUES ('268493467', '268494705', '1788', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151057');
INSERT INTO `items` VALUES ('268493467', '268494706', '1869', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151057');
INSERT INTO `items` VALUES ('268493467', '268494707', '1864', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151057');
INSERT INTO `items` VALUES ('268497182', '268494719', '6672', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268497182', '268494721', '1869', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268494184', '268494724', '9273', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268494184', '268494726', '9270', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268493578', '268494738', '1866', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006323959');
INSERT INTO `items` VALUES ('268493578', '268494739', '1885', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006323959');
INSERT INTO `items` VALUES ('268493896', '268494746', '1879', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032931918');
INSERT INTO `items` VALUES ('268493467', '268494747', '462', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151057');
INSERT INTO `items` VALUES ('268493467', '268494748', '426', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151055');
INSERT INTO `items` VALUES ('268494356', '268494760', '1869', '39', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494184', '268494763', '1458', '987', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268494184', '268494764', '1459', '1000', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268494751', '268494771', '57', '879223850', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268494406', '268494774', '641', '1', '6', 'PAPERDOLL', '8', '0', '0', '-1', '1770950100502');
INSERT INTO `items` VALUES ('268493896', '268494789', '5549', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032985340');
INSERT INTO `items` VALUES ('268493896', '268494790', '4044', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771032985341');
INSERT INTO `items` VALUES ('268494406', '268494794', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493578', '268494796', '2626', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1773006382545');
INSERT INTO `items` VALUES ('268493578', '268494798', '864', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1773006391022');
INSERT INTO `items` VALUES ('268493578', '268494799', '864', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1773006396089');
INSERT INTO `items` VALUES ('268493578', '268494801', '895', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1773006398159');
INSERT INTO `items` VALUES ('268493578', '268494802', '895', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1773006398995');
INSERT INTO `items` VALUES ('268493578', '268494803', '926', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1773006401082');
INSERT INTO `items` VALUES ('268493578', '268494806', '358', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1773006420430');
INSERT INTO `items` VALUES ('268493578', '268494807', '2380', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1773006422306');
INSERT INTO `items` VALUES ('268493578', '268494810', '673', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006427802');
INSERT INTO `items` VALUES ('268493578', '268494811', '5718', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1773006436619');
INSERT INTO `items` VALUES ('268493896', '268494818', '1864', '19', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268493896', '268494819', '1869', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268494406', '268494826', '1872', '37', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494406', '268494829', '1865', '227', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100488');
INSERT INTO `items` VALUES ('268494406', '268494830', '1463', '2160', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100488');
INSERT INTO `items` VALUES ('268493605', '268494833', '728', '885', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493605', '268494835', '1539', '1169', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268494406', '268494838', '6608', '1', '1500', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493605', '268494839', '3031', '600', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735304');
INSERT INTO `items` VALUES ('268493578', '268494840', '5734', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1773006440481');
INSERT INTO `items` VALUES ('268493578', '268494841', '2416', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1773006445200');
INSERT INTO `items` VALUES ('268494406', '268494851', '2407', '1', '6', 'PAPERDOLL', '11', '0', '0', '-1', '1770950100505');
INSERT INTO `items` VALUES ('268494324', '268494854', '159', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984606');
INSERT INTO `items` VALUES ('268494324', '268494855', '2493', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984607');
INSERT INTO `items` VALUES ('268494324', '268494856', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1771554984626');
INSERT INTO `items` VALUES ('268494324', '268494857', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1771554984622');
INSERT INTO `items` VALUES ('268494324', '268494858', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1771554984624');
INSERT INTO `items` VALUES ('268494324', '268494859', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1771554984623');
INSERT INTO `items` VALUES ('268493605', '268494860', '1882', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268494324', '268494862', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1771554984618');
INSERT INTO `items` VALUES ('268494324', '268494863', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1771554984615');
INSERT INTO `items` VALUES ('268494324', '268494864', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1771554984616');
INSERT INTO `items` VALUES ('268494324', '268494865', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1771554984610');
INSERT INTO `items` VALUES ('268494324', '268494866', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1771554984612');
INSERT INTO `items` VALUES ('268494324', '268494867', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1771554984614');
INSERT INTO `items` VALUES ('268494324', '268494868', '3031', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268494869', '728', '3253', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494406', '268494875', '1341', '7240', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494751', '268494880', '1539', '2507', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268493578', '268494881', '1465', '50000', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006472486');
INSERT INTO `items` VALUES ('268509776', '268494884', '4065', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697737462');
INSERT INTO `items` VALUES ('268509776', '268494885', '4065', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697737462');
INSERT INTO `items` VALUES ('268494406', '268494886', '512', '1', '6', 'PAPERDOLL', '6', '0', '0', '-1', '1770950100499');
INSERT INTO `items` VALUES ('268494406', '268494889', '5767', '1', '6', 'PAPERDOLL', '10', '0', '0', '-1', '1770950100504');
INSERT INTO `items` VALUES ('268494406', '268494894', '5779', '1', '6', 'PAPERDOLL', '13', '0', '0', '-1', '1770950100506');
INSERT INTO `items` VALUES ('268493578', '268494897', '1873', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006487741');
INSERT INTO `items` VALUES ('268494751', '268494911', '8563', '1', '0', 'PAPERDOLL', '16', '0', '0', '-1', '1779163691672');
INSERT INTO `items` VALUES ('268494324', '268494917', '1539', '3472', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494324', '268494918', '1341', '50000', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494324', '268494919', '8595', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494324', '268494920', '8596', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494324', '268494933', '6035', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984601');
INSERT INTO `items` VALUES ('268494324', '268494934', '1868', '120', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984601');
INSERT INTO `items` VALUES ('268493578', '268494946', '1875', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006516125');
INSERT INTO `items` VALUES ('268631868', '268494955', '6393', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905675972');
INSERT INTO `items` VALUES ('268509776', '268494956', '1885', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697814531');
INSERT INTO `items` VALUES ('268631868', '268494957', '6392', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905675972');
INSERT INTO `items` VALUES ('268494228', '268494967', '57', '893170926', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273556');
INSERT INTO `items` VALUES ('268493142', '268494968', '57', '79953228', '0', 'INVENTORY', '0', '0', '0', '-1', '1756151288916');
INSERT INTO `items` VALUES ('268494356', '268494979', '116', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1777429606451');
INSERT INTO `items` VALUES ('268494228', '268494981', '3470', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654098');
INSERT INTO `items` VALUES ('268494667', '268495000', '20', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430347');
INSERT INTO `items` VALUES ('268494667', '268495001', '43', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430348');
INSERT INTO `items` VALUES ('268494667', '268495002', '177', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430349');
INSERT INTO `items` VALUES ('268493309', '268495010', '1868', '581', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268494667', '268495012', '3948', '3', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430349');
INSERT INTO `items` VALUES ('268493309', '268495017', '1832', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268494751', '268495026', '1871', '37', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495032', '1866', '36', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495035', '6392', '2483', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495040', '6393', '1955', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495042', '4037', '657', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495044', '6360', '1542', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495045', '6362', '1357', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268493467', '268495053', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151051');
INSERT INTO `items` VALUES ('268494406', '268495057', '3952', '7607', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493896', '268495080', '4042', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771033281071');
INSERT INTO `items` VALUES ('268494406', '268495096', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494324', '268495108', '1871', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984601');
INSERT INTO `items` VALUES ('268493578', '268495111', '4180', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006602200');
INSERT INTO `items` VALUES ('268498467', '268495130', '1878', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167810');
INSERT INTO `items` VALUES ('268494184', '268495134', '1868', '16', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268494667', '268495135', '1872', '79', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430349');
INSERT INTO `items` VALUES ('268494667', '268495136', '1867', '91', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430349');
INSERT INTO `items` VALUES ('268494184', '268495142', '1871', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268494184', '268495143', '1831', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268497182', '268495156', '728', '1769', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268509776', '268495165', '1806', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697960989');
INSERT INTO `items` VALUES ('268509776', '268495179', '4080', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770698006575');
INSERT INTO `items` VALUES ('268509776', '268495180', '4081', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770698006575');
INSERT INTO `items` VALUES ('268494324', '268495190', '1873', '21', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984601');
INSERT INTO `items` VALUES ('268494324', '268495191', '2176', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494751', '268495194', '6341', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495195', '2508', '46561', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494324', '268495196', '6037', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494751', '268495197', '6345', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495198', '6343', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268493605', '268495217', '5153', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268494667', '268495227', '4037', '175', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430322');
INSERT INTO `items` VALUES ('268493605', '268495229', '5278', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268494184', '268495233', '8563', '1', '0', 'PAPERDOLL', '16', '0', '0', '-1', '1771025382126');
INSERT INTO `items` VALUES ('268494228', '268495235', '1538', '22', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268497394', '268495236', '4099', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777434796755');
INSERT INTO `items` VALUES ('268494406', '268495241', '6392', '495', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494406', '268495243', '6391', '668', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494406', '268495246', '5558', '254', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494406', '268495247', '6393', '174', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494406', '268495249', '5557', '104', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494228', '268495266', '2508', '1000', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268493913', '268495279', '1831', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268498467', '268495292', '6667', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167810');
INSERT INTO `items` VALUES ('268494324', '268495295', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494184', '268495296', '1873', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268494324', '268495297', '7694', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1771554984627');
INSERT INTO `items` VALUES ('268494356', '268495301', '1870', '16', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268493876', '268495319', '1869', '19', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268493876', '268495320', '1864', '27', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268493578', '268495323', '1881', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773006834522');
INSERT INTO `items` VALUES ('268494228', '268495334', '3031', '500', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494228', '268495339', '3883', '69', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494324', '268495340', '1884', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268495341', '1878', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494751', '268495348', '5497', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495358', '7646', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268495359', '7647', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494228', '268495371', '1458', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494406', '268495406', '6667', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493896', '268495415', '1984', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771033648666');
INSERT INTO `items` VALUES ('268494751', '268495419', '7648', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268494751', '268495455', '15009', '1', '15', 'PAPERDOLL', '4', '0', '0', '-1', '1779163691660');
INSERT INTO `items` VALUES ('268494324', '268495459', '297', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1771554984620');
INSERT INTO `items` VALUES ('268493309', '268495470', '4043', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493913', '268495498', '6360', '1781', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493913', '268495499', '6362', '632', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268495502', '268495503', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483919');
INSERT INTO `items` VALUES ('268495502', '268495504', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483928');
INSERT INTO `items` VALUES ('268495502', '268495505', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483929');
INSERT INTO `items` VALUES ('268495502', '268495506', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483930');
INSERT INTO `items` VALUES ('268494751', '268495507', '3470', '63', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268495502', '268495509', '4037', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483930');
INSERT INTO `items` VALUES ('268495502', '268495512', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483930');
INSERT INTO `items` VALUES ('268495502', '268495513', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483930');
INSERT INTO `items` VALUES ('268495502', '268495514', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483930');
INSERT INTO `items` VALUES ('268495502', '268495516', '15017', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483930');
INSERT INTO `items` VALUES ('268495502', '268495523', '57', '73463849', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483930');
INSERT INTO `items` VALUES ('268498467', '268495541', '159', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1773110361346');
INSERT INTO `items` VALUES ('268498467', '268495542', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1773110367294');
INSERT INTO `items` VALUES ('268498467', '268495543', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1773110368091');
INSERT INTO `items` VALUES ('268498467', '268495544', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1773110369945');
INSERT INTO `items` VALUES ('268498467', '268495545', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1773110370614');
INSERT INTO `items` VALUES ('268498467', '268495546', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1773110372229');
INSERT INTO `items` VALUES ('268498467', '268495547', '2493', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1773110376797');
INSERT INTO `items` VALUES ('268498467', '268495548', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1773110378511');
INSERT INTO `items` VALUES ('268498467', '268495549', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1773110380255');
INSERT INTO `items` VALUES ('268498467', '268495550', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1773110381967');
INSERT INTO `items` VALUES ('268498467', '268495551', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1773110383681');
INSERT INTO `items` VALUES ('268498467', '268495552', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1773110385612');
INSERT INTO `items` VALUES ('268523291', '268495553', '1873', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172565');
INSERT INTO `items` VALUES ('268498467', '268495554', '3031', '454', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110412574');
INSERT INTO `items` VALUES ('268498467', '268495555', '1458', '438', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110415470');
INSERT INTO `items` VALUES ('268498467', '268495556', '728', '2149', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110422454');
INSERT INTO `items` VALUES ('268498467', '268495557', '1539', '8576', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110425447');
INSERT INTO `items` VALUES ('268493876', '268495572', '1865', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569338');
INSERT INTO `items` VALUES ('268497182', '268495573', '7266', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268493896', '268495618', '1985', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771033845063');
INSERT INTO `items` VALUES ('268495632', '268495633', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1768716444980');
INSERT INTO `items` VALUES ('268495632', '268495634', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1768716444978');
INSERT INTO `items` VALUES ('268495632', '268495635', '10', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1768716444977');
INSERT INTO `items` VALUES ('268495632', '268495636', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768716444976');
INSERT INTO `items` VALUES ('268495632', '268495637', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768716444976');
INSERT INTO `items` VALUES ('268495632', '268495646', '57', '1959', '0', 'INVENTORY', '0', '0', '0', '-1', '1768716444976');
INSERT INTO `items` VALUES ('268494228', '268495658', '1895', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494324', '268495659', '1864', '132', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494324', '268495667', '1895', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494406', '268495669', '5559', '24', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493896', '268495677', '1916', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771033885464');
INSERT INTO `items` VALUES ('268494324', '268495678', '1869', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268493896', '268495689', '1832', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771033937461');
INSERT INTO `items` VALUES ('268493896', '268495694', '2169', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771033941465');
INSERT INTO `items` VALUES ('268494751', '268495695', '1865', '45', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163715932');
INSERT INTO `items` VALUES ('268494751', '268495696', '1879', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163715932');
INSERT INTO `items` VALUES ('268494751', '268495697', '1870', '25', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163756120');
INSERT INTO `items` VALUES ('268494751', '268495698', '1875', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163756120');
INSERT INTO `items` VALUES ('268494751', '268495735', '5524', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268494324', '268495736', '4180', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984603');
INSERT INTO `items` VALUES ('268494751', '268495738', '7667', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268493896', '268495739', '1419', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034110724');
INSERT INTO `items` VALUES ('268493913', '268495747', '231', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1770957121650');
INSERT INTO `items` VALUES ('268493913', '268495748', '852', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1770957121641');
INSERT INTO `items` VALUES ('268493913', '268495749', '852', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1770957121640');
INSERT INTO `items` VALUES ('268493913', '268495751', '886', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1770957121646');
INSERT INTO `items` VALUES ('268493913', '268495752', '886', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1770957121647');
INSERT INTO `items` VALUES ('268493913', '268495753', '119', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1770957121645');
INSERT INTO `items` VALUES ('268493913', '268495754', '400', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1770957121656');
INSERT INTO `items` VALUES ('268493913', '268495755', '420', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1770957121657');
INSERT INTO `items` VALUES ('268494324', '268495759', '1986', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268498467', '268495763', '1874', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110719133');
INSERT INTO `items` VALUES ('268498467', '268495764', '1884', '46', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110719133');
INSERT INTO `items` VALUES ('268498467', '268495765', '2972', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110719133');
INSERT INTO `items` VALUES ('268493913', '268495794', '2436', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1770957121658');
INSERT INTO `items` VALUES ('268493913', '268495795', '2460', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1770957121654');
INSERT INTO `items` VALUES ('268493913', '268495796', '2497', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1770957121651');
INSERT INTO `items` VALUES ('268493913', '268495797', '2414', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1770957121649');
INSERT INTO `items` VALUES ('268509776', '268495807', '1918', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770698702171');
INSERT INTO `items` VALUES ('268494228', '268495832', '1872', '697', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273556');
INSERT INTO `items` VALUES ('268494228', '268495833', '1867', '1102', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273556');
INSERT INTO `items` VALUES ('268494228', '268495835', '1344', '15950', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273556');
INSERT INTO `items` VALUES ('268494228', '268495836', '1868', '1437', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268494228', '268495838', '1889', '40', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268494228', '268495839', '1873', '267', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268498467', '268495847', '1341', '1434', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110812278');
INSERT INTO `items` VALUES ('268495632', '268495848', '1067', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1768716444976');
INSERT INTO `items` VALUES ('268495874', '268495875', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1770558183680');
INSERT INTO `items` VALUES ('268495874', '268495876', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1770558183679');
INSERT INTO `items` VALUES ('268495874', '268495877', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183676');
INSERT INTO `items` VALUES ('268495874', '268495878', '2370', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1770558183678');
INSERT INTO `items` VALUES ('268495874', '268495879', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183676');
INSERT INTO `items` VALUES ('268493913', '268495880', '728', '590', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121637');
INSERT INTO `items` VALUES ('268493913', '268495881', '1539', '690', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121637');
INSERT INTO `items` VALUES ('268493913', '268495882', '3031', '470', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121637');
INSERT INTO `items` VALUES ('268493913', '268495883', '1540', '4988', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121637');
INSERT INTO `items` VALUES ('268495874', '268495884', '57', '36487896', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183677');
INSERT INTO `items` VALUES ('268493913', '268495885', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121637');
INSERT INTO `items` VALUES ('268493913', '268495886', '7694', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493309', '268495891', '4037', '371', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268494406', '268495898', '1878', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100491');
INSERT INTO `items` VALUES ('268493896', '268495915', '948', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034134263');
INSERT INTO `items` VALUES ('268494406', '268495921', '1928', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493876', '268495923', '280', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569342');
INSERT INTO `items` VALUES ('268494228', '268495927', '1882', '178', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268523291', '268495936', '1905', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172556');
INSERT INTO `items` VALUES ('268493913', '268495962', '1785', '400', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493913', '268495963', '5592', '29', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493896', '268495967', '4052', '19', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034148664');
INSERT INTO `items` VALUES ('268494324', '268495984', '4144', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268493896', '268495985', '4114', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034251878');
INSERT INTO `items` VALUES ('268493876', '268495986', '1463', '2116', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569342');
INSERT INTO `items` VALUES ('268493896', '268495987', '8330', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034251878');
INSERT INTO `items` VALUES ('268493896', '268495988', '1867', '66', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034251878');
INSERT INTO `items` VALUES ('268493896', '268495989', '1881', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034251878');
INSERT INTO `items` VALUES ('268493896', '268495990', '6037', '42', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034251878');
INSERT INTO `items` VALUES ('268493896', '268495994', '1872', '70', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034274057');
INSERT INTO `items` VALUES ('268494751', '268496002', '5556', '78', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691648');
INSERT INTO `items` VALUES ('268493896', '268496005', '4053', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034312266');
INSERT INTO `items` VALUES ('268493896', '268496006', '4154', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034335464');
INSERT INTO `items` VALUES ('268494228', '268496023', '5277', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777090156034');
INSERT INTO `items` VALUES ('268494228', '268496031', '4044', '19', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268493876', '268496034', '225', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1770474569351');
INSERT INTO `items` VALUES ('268493876', '268496035', '2426', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569343');
INSERT INTO `items` VALUES ('268493876', '268496036', '417', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1770474569356');
INSERT INTO `items` VALUES ('268493876', '268496037', '395', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1770474569354');
INSERT INTO `items` VALUES ('268493876', '268496038', '2448', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1770474569353');
INSERT INTO `items` VALUES ('268493876', '268496039', '2424', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1770474569358');
INSERT INTO `items` VALUES ('268493876', '268496040', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1770474569350');
INSERT INTO `items` VALUES ('268493876', '268496041', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1770474569348');
INSERT INTO `items` VALUES ('268493876', '268496042', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1770474569349');
INSERT INTO `items` VALUES ('268493876', '268496043', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1770474569344');
INSERT INTO `items` VALUES ('268493876', '268496044', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1770474569345');
INSERT INTO `items` VALUES ('268493876', '268496045', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1770474569347');
INSERT INTO `items` VALUES ('268493876', '268496061', '1539', '497', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268493876', '268496062', '728', '231', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268494751', '268496069', '1869', '28', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163871530');
INSERT INTO `items` VALUES ('268494751', '268496070', '1864', '75', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163871530');
INSERT INTO `items` VALUES ('268494751', '268496073', '1881', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163907930');
INSERT INTO `items` VALUES ('268493896', '268496076', '1871', '16', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268493896', '268496077', '1866', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268493896', '268496080', '6666', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268494228', '268496088', '5508', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268494228', '268496089', '5496', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268494228', '268496090', '5509', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268494228', '268496091', '5497', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268494228', '268496092', '5549', '81', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268493913', '268496101', '1464', '49049', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268523291', '268496133', '6668', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172556');
INSERT INTO `items` VALUES ('268494324', '268496139', '1865', '65', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984602');
INSERT INTO `items` VALUES ('268494324', '268496140', '1866', '46', '0', 'INVENTORY', '0', '0', '0', '-1', '1771554984601');
INSERT INTO `items` VALUES ('268494406', '268496146', '1866', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268495874', '268496168', '5789', '398', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183677');
INSERT INTO `items` VALUES ('268523291', '268496181', '1875', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172557');
INSERT INTO `items` VALUES ('268493876', '268496197', '1870', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268493876', '268496198', '2176', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268493913', '268496201', '9577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268495874', '268496215', '3031', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183677');
INSERT INTO `items` VALUES ('268495874', '268496217', '728', '500', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183677');
INSERT INTO `items` VALUES ('268495874', '268496218', '1539', '500', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183677');
INSERT INTO `items` VALUES ('268493896', '268496220', '2089', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034435063');
INSERT INTO `items` VALUES ('268495874', '268496222', '1463', '10000', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183675');
INSERT INTO `items` VALUES ('268495874', '268496223', '159', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183675');
INSERT INTO `items` VALUES ('268495874', '268496225', '2493', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183674');
INSERT INTO `items` VALUES ('268495874', '268496226', '2425', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183666');
INSERT INTO `items` VALUES ('268495874', '268496227', '2449', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183667');
INSERT INTO `items` VALUES ('268495874', '268496228', '2378', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183668');
INSERT INTO `items` VALUES ('268495874', '268496229', '352', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183669');
INSERT INTO `items` VALUES ('268495874', '268496239', '2411', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183669');
INSERT INTO `items` VALUES ('268495874', '268496243', '882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183670');
INSERT INTO `items` VALUES ('268498467', '268496251', '1895', '57', '0', 'INVENTORY', '0', '0', '0', '-1', '1773111284789');
INSERT INTO `items` VALUES ('268497182', '268496255', '7263', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268493896', '268496274', '4096', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034617460');
INSERT INTO `items` VALUES ('268493896', '268496275', '4091', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034617460');
INSERT INTO `items` VALUES ('268493876', '268496291', '1871', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268493876', '268496298', '5273', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268495874', '268496305', '882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183671');
INSERT INTO `items` VALUES ('268498467', '268496306', '1881', '16', '0', 'INVENTORY', '0', '0', '0', '-1', '1773111333544');
INSERT INTO `items` VALUES ('268493876', '268496311', '734', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268493896', '268496328', '4070', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034705463');
INSERT INTO `items` VALUES ('268494751', '268496339', '6671', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268496343', '4044', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268496344', '1894', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268496346', '4039', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494751', '268496348', '1885', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268494228', '268496351', '1894', '29', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268494228', '268496352', '4042', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268493913', '268496364', '1871', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493913', '268496365', '1866', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268494751', '268496366', '5535', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779164210731');
INSERT INTO `items` VALUES ('268494356', '268496378', '118', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1777429606450');
INSERT INTO `items` VALUES ('268495874', '268496426', '851', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183672');
INSERT INTO `items` VALUES ('268494356', '268496439', '1864', '64', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268498467', '268496463', '1876', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773111426902');
INSERT INTO `items` VALUES ('268498467', '268496475', '1882', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773111551805');
INSERT INTO `items` VALUES ('268493896', '268496483', '4124', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034767864');
INSERT INTO `items` VALUES ('268495874', '268496488', '851', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183673');
INSERT INTO `items` VALUES ('268495502', '268496490', '1067', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483931');
INSERT INTO `items` VALUES ('268495874', '268496492', '914', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770558183665');
INSERT INTO `items` VALUES ('268493896', '268496496', '4090', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034862043');
INSERT INTO `items` VALUES ('268493896', '268496497', '4099', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771034862043');
INSERT INTO `items` VALUES ('268495502', '268496508', '68', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779032483939');
INSERT INTO `items` VALUES ('268495502', '268496509', '711', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779032483942');
INSERT INTO `items` VALUES ('268495502', '268496510', '715', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1779032483943');
INSERT INTO `items` VALUES ('268495502', '268496511', '908', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779032483934');
INSERT INTO `items` VALUES ('268495502', '268496512', '115', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779032483932');
INSERT INTO `items` VALUES ('268495502', '268496513', '115', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779032483933');
INSERT INTO `items` VALUES ('268495502', '268496514', '877', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1779032483935');
INSERT INTO `items` VALUES ('268495502', '268496515', '877', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779032483937');
INSERT INTO `items` VALUES ('268495502', '268496517', '1463', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483926');
INSERT INTO `items` VALUES ('268495502', '268496520', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483920');
INSERT INTO `items` VALUES ('268495502', '268496521', '7694', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483920');
INSERT INTO `items` VALUES ('268493913', '268496539', '1864', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493913', '268496540', '1419', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493913', '268496559', '1869', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770957121639');
INSERT INTO `items` VALUES ('268493876', '268496652', '6037', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268493876', '268496654', '1799', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268495502', '268496737', '1835', '1000', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483920');
INSERT INTO `items` VALUES ('268495502', '268496738', '1540', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268495502', '268496741', '17', '1000', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268495502', '268496743', '3031', '52', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268495502', '268496745', '1458', '87', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268493309', '268496891', '5575', '3824000', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268496895', '3958', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268496900', '3470', '54', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268496901', '7694', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898614');
INSERT INTO `items` VALUES ('268493309', '268496911', '193', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268495502', '268497029', '1869', '161', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268495502', '268497030', '1864', '290', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268493896', '268497085', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1771031469689');
INSERT INTO `items` VALUES ('268493896', '268497087', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1771031469682');
INSERT INTO `items` VALUES ('268493896', '268497089', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1771031469684');
INSERT INTO `items` VALUES ('268493896', '268497091', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1771031469699');
INSERT INTO `items` VALUES ('268493896', '268497092', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1771031469695');
INSERT INTO `items` VALUES ('268493896', '268497093', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1771031469698');
INSERT INTO `items` VALUES ('268493896', '268497094', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1771031469696');
INSERT INTO `items` VALUES ('268493896', '268497095', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1771031469690');
INSERT INTO `items` VALUES ('268493896', '268497096', '728', '421', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469675');
INSERT INTO `items` VALUES ('268493896', '268497097', '1539', '964', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469675');
INSERT INTO `items` VALUES ('268493896', '268497098', '3031', '495', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469676');
INSERT INTO `items` VALUES ('268494667', '268497151', '1873', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1777434077437');
INSERT INTO `items` VALUES ('268493309', '268497230', '6668', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268497231', '1865', '127', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268497232', '1884', '39', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268497239', '1341', '1176', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268497256', '1878', '29', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268497304', '1870', '125', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493896', '268497314', '1884', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469678');
INSERT INTO `items` VALUES ('268493896', '268497315', '1878', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469678');
INSERT INTO `items` VALUES ('268493309', '268497338', '1879', '20', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493896', '268497343', '6362', '50', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469678');
INSERT INTO `items` VALUES ('268493896', '268497344', '6360', '158', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469678');
INSERT INTO `items` VALUES ('268493309', '268497364', '1871', '51', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268497394', '268497399', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777433602421');
INSERT INTO `items` VALUES ('268497394', '268497400', '57', '884757652', '0', 'INVENTORY', '0', '0', '0', '-1', '1777433628159');
INSERT INTO `items` VALUES ('268495502', '268497404', '1792', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268497394', '268497408', '1496', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777433602421');
INSERT INTO `items` VALUES ('268493896', '268497422', '4991', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771036179862');
INSERT INTO `items` VALUES ('268494184', '268497423', '1870', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268494356', '268497456', '1880', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494751', '268497493', '1880', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1779165244112');
INSERT INTO `items` VALUES ('268493467', '268497497', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151049');
INSERT INTO `items` VALUES ('268494406', '268497500', '3470', '36', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268493309', '268497532', '1869', '106', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268497533', '1864', '247', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493896', '268497538', '1873', '84', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469675');
INSERT INTO `items` VALUES ('268493896', '268497540', '1868', '431', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469678');
INSERT INTO `items` VALUES ('268493896', '268497543', '1865', '221', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469678');
INSERT INTO `items` VALUES ('268493896', '268497544', '1870', '24', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469678');
INSERT INTO `items` VALUES ('268494228', '268497586', '5536', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268493896', '268497593', '4150', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771036247119');
INSERT INTO `items` VALUES ('268493309', '268497598', '5220', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268493309', '268497631', '1866', '50', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268493896', '268497637', '4142', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469679');
INSERT INTO `items` VALUES ('268493896', '268497638', '1918', '24', '0', 'INVENTORY', '0', '0', '0', '-1', '1771031469675');
INSERT INTO `items` VALUES ('268493467', '268497654', '4', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151032');
INSERT INTO `items` VALUES ('268493467', '268497655', '7', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151033');
INSERT INTO `items` VALUES ('268493876', '268497671', '1866', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268497394', '268497692', '4037', '67', '0', 'INVENTORY', '0', '0', '0', '-1', '1777433602417');
INSERT INTO `items` VALUES ('268495502', '268497793', '736', '42', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268494356', '268497804', '6666', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268493876', '268497866', '1878', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268493876', '268497867', '6666', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268494667', '268497925', '1869', '20', '0', 'INVENTORY', '0', '0', '0', '-1', '1777434077436');
INSERT INTO `items` VALUES ('268497927', '268497928', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1770936954030');
INSERT INTO `items` VALUES ('268497927', '268497929', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1770936954029');
INSERT INTO `items` VALUES ('268497927', '268497930', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936954027');
INSERT INTO `items` VALUES ('268497927', '268497931', '2369', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1770936954028');
INSERT INTO `items` VALUES ('268497927', '268497932', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936954027');
INSERT INTO `items` VALUES ('268497927', '268497933', '57', '88876604', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936954027');
INSERT INTO `items` VALUES ('268497927', '268497942', '1070', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936954027');
INSERT INTO `items` VALUES ('268497927', '268497943', '5789', '143', '0', 'INVENTORY', '0', '0', '0', '-1', '1770936954027');
INSERT INTO `items` VALUES ('268494667', '268497947', '4167', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777427399703');
INSERT INTO `items` VALUES ('268494667', '268497948', '1832', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777427399703');
INSERT INTO `items` VALUES ('268493876', '268497998', '1898', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268498074', '268498075', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125771538');
INSERT INTO `items` VALUES ('268498074', '268498076', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125771536');
INSERT INTO `items` VALUES ('268498074', '268498077', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769562');
INSERT INTO `items` VALUES ('268498074', '268498078', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125771534');
INSERT INTO `items` VALUES ('268498074', '268498079', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769562');
INSERT INTO `items` VALUES ('268498074', '268498082', '15022', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769568');
INSERT INTO `items` VALUES ('268498074', '268498083', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769568');
INSERT INTO `items` VALUES ('268498074', '268498084', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769568');
INSERT INTO `items` VALUES ('268498074', '268498085', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769568');
INSERT INTO `items` VALUES ('268498074', '268498086', '15018', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769569');
INSERT INTO `items` VALUES ('268498074', '268498087', '15017', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769569');
INSERT INTO `items` VALUES ('268498074', '268498088', '15023', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769569');
INSERT INTO `items` VALUES ('268498074', '268498089', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769569');
INSERT INTO `items` VALUES ('268498074', '268498091', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125769569');
INSERT INTO `items` VALUES ('268498467', '268498092', '5220', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773112508042');
INSERT INTO `items` VALUES ('268493467', '268498095', '21', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151034');
INSERT INTO `items` VALUES ('268493467', '268498096', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151035');
INSERT INTO `items` VALUES ('268498107', '268498108', '425', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471196');
INSERT INTO `items` VALUES ('268498107', '268498109', '461', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471197');
INSERT INTO `items` VALUES ('268498107', '268498110', '2368', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471195');
INSERT INTO `items` VALUES ('268498107', '268498111', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498107', '268498114', '15022', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498107', '268498115', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498107', '268498116', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498107', '268498117', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498118', '268498119', '1147', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1773969758026');
INSERT INTO `items` VALUES ('268498118', '268498120', '1146', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1773969758024');
INSERT INTO `items` VALUES ('268498118', '268498121', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758022');
INSERT INTO `items` VALUES ('268498118', '268498122', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758023');
INSERT INTO `items` VALUES ('268498118', '268498123', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758021');
INSERT INTO `items` VALUES ('268498118', '268498124', '4037', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758021');
INSERT INTO `items` VALUES ('268498107', '268498125', '15018', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498118', '268498127', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758021');
INSERT INTO `items` VALUES ('268498118', '268498128', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758020');
INSERT INTO `items` VALUES ('268498118', '268498129', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758020');
INSERT INTO `items` VALUES ('268498107', '268498130', '15023', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498118', '268498131', '15017', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758020');
INSERT INTO `items` VALUES ('268498118', '268498132', '15023', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758020');
INSERT INTO `items` VALUES ('268498118', '268498133', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758020');
INSERT INTO `items` VALUES ('268498118', '268498134', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969758020');
INSERT INTO `items` VALUES ('268498107', '268498135', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498107', '268498136', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071471193');
INSERT INTO `items` VALUES ('268498107', '268498137', '57', '24111374', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071493924');
INSERT INTO `items` VALUES ('268498107', '268498138', '177', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1777071514203');
INSERT INTO `items` VALUES ('268498107', '268498139', '1101', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1777071514203');
INSERT INTO `items` VALUES ('268498107', '268498140', '1104', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1777071514203');
INSERT INTO `items` VALUES ('268498107', '268498141', '908', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1777071514203');
INSERT INTO `items` VALUES ('268498107', '268498142', '115', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1777071514203');
INSERT INTO `items` VALUES ('268498107', '268498143', '115', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1777071514203');
INSERT INTO `items` VALUES ('268498107', '268498144', '877', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1777071514204');
INSERT INTO `items` VALUES ('268498107', '268498145', '877', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1777071514204');
INSERT INTO `items` VALUES ('268493707', '268498146', '57', '97429289', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881201090');
INSERT INTO `items` VALUES ('268498107', '268498148', '3948', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071514204');
INSERT INTO `items` VALUES ('268494751', '268498152', '4041', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779165703322');
INSERT INTO `items` VALUES ('268493707', '268498154', '1069', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881251685');
INSERT INTO `items` VALUES ('268494667', '268498159', '1876', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777427460024');
INSERT INTO `items` VALUES ('268494667', '268498166', '4915', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777427502205');
INSERT INTO `items` VALUES ('268494667', '268498167', '4921', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777427502205');
INSERT INTO `items` VALUES ('268494356', '268498169', '159', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1777429606453');
INSERT INTO `items` VALUES ('268493856', '268498182', '1879', '17', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498183', '1865', '125', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498184', '1870', '62', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498185', '1344', '3545', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498186', '1871', '56', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268498107', '268498197', '1496', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071587225');
INSERT INTO `items` VALUES ('268498107', '268498198', '5789', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071587225');
INSERT INTO `items` VALUES ('268498467', '268498205', '1875', '19', '0', 'INVENTORY', '0', '0', '0', '-1', '1773112650502');
INSERT INTO `items` VALUES ('268494356', '268498213', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1777429606451');
INSERT INTO `items` VALUES ('268498467', '268498230', '2149', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1773112792131');
INSERT INTO `items` VALUES ('268498467', '268498231', '1939', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773112793521');
INSERT INTO `items` VALUES ('268498467', '268498232', '1939', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773112793521');
INSERT INTO `items` VALUES ('268493707', '268498236', '1868', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881413584');
INSERT INTO `items` VALUES ('268493467', '268498253', '1870', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151035');
INSERT INTO `items` VALUES ('268493467', '268498254', '1060', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151035');
INSERT INTO `items` VALUES ('268493309', '268498290', '1539', '591', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268493309', '268498291', '728', '467', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268498074', '268498298', '57', '24103384', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125812060');
INSERT INTO `items` VALUES ('268498074', '268498303', '1067', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125887552');
INSERT INTO `items` VALUES ('268498074', '268498304', '5789', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1776125887553');
INSERT INTO `items` VALUES ('268498118', '268498317', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969814112');
INSERT INTO `items` VALUES ('268498118', '268498318', '7694', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1773969814113');
INSERT INTO `items` VALUES ('268498118', '268498319', '68', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1773969816753');
INSERT INTO `items` VALUES ('268498118', '268498320', '711', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969816753');
INSERT INTO `items` VALUES ('268498118', '268498321', '715', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969816753');
INSERT INTO `items` VALUES ('268498118', '268498322', '908', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1773969816753');
INSERT INTO `items` VALUES ('268498118', '268498323', '115', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1773969816753');
INSERT INTO `items` VALUES ('268498118', '268498324', '115', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1773969816753');
INSERT INTO `items` VALUES ('268498118', '268498325', '877', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1773969816754');
INSERT INTO `items` VALUES ('268498118', '268498326', '877', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1773969816754');
INSERT INTO `items` VALUES ('268498118', '268498328', '1463', '5002', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969816754');
INSERT INTO `items` VALUES ('268498118', '268498329', '57', '48473788', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969867089');
INSERT INTO `items` VALUES ('268494356', '268498330', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1777429606449');
INSERT INTO `items` VALUES ('268494356', '268498331', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1777429606449');
INSERT INTO `items` VALUES ('268498118', '268498332', '5789', '600', '0', 'INVENTORY', '0', '0', '0', '-1', '1773969878799');
INSERT INTO `items` VALUES ('268494356', '268498334', '2493', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1777429606453');
INSERT INTO `items` VALUES ('268494356', '268498335', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1777429606456');
INSERT INTO `items` VALUES ('268494356', '268498336', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1777429606454');
INSERT INTO `items` VALUES ('268494356', '268498337', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1777429606455');
INSERT INTO `items` VALUES ('268494356', '268498338', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1777429606455');
INSERT INTO `items` VALUES ('268494356', '268498339', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1777429606452');
INSERT INTO `items` VALUES ('268494356', '268498340', '1463', '45074', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268498341', '728', '969', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268498342', '1539', '987', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268498343', '3031', '432', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268498344', '1785', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268498345', '1458', '439', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268498346', '1459', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268498467', '268498347', '2269', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773112881004');
INSERT INTO `items` VALUES ('268493707', '268498382', '1867', '22', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881452711');
INSERT INTO `items` VALUES ('268493707', '268498383', '1872', '20', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881452711');
INSERT INTO `items` VALUES ('268493707', '268498384', '1799', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881452711');
INSERT INTO `items` VALUES ('268493856', '268498388', '1864', '100', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498390', '1875', '8', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498391', '1876', '4', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493707', '268498392', '1803', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881519923');
INSERT INTO `items` VALUES ('268493856', '268498397', '1880', '9', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498403', '1869', '43', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493707', '268498409', '728', '685', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881650001');
INSERT INTO `items` VALUES ('268493707', '268498411', '1539', '962', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881654414');
INSERT INTO `items` VALUES ('268493707', '268498412', '9268', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881664820');
INSERT INTO `items` VALUES ('268493856', '268498413', '4042', '3', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268493856', '268498414', '1895', '6', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268493856', '268498418', '1885', '6', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268493856', '268498421', '5220', '3', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493707', '268498452', '1870', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881692528');
INSERT INTO `items` VALUES ('268493707', '268498453', '1817', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881692528');
INSERT INTO `items` VALUES ('268493707', '268498454', '1865', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881692528');
INSERT INTO `items` VALUES ('268498467', '268498469', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167808');
INSERT INTO `items` VALUES ('268498467', '268498470', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167807');
INSERT INTO `items` VALUES ('268498467', '268498471', '2370', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167806');
INSERT INTO `items` VALUES ('268498467', '268498472', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498481', '15023', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167810');
INSERT INTO `items` VALUES ('268498467', '268498482', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167810');
INSERT INTO `items` VALUES ('268498467', '268498483', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167810');
INSERT INTO `items` VALUES ('268498467', '268498484', '57', '47397977', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498497', '1463', '50002', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498499', '3470', '38', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167803');
INSERT INTO `items` VALUES ('268498467', '268498500', '7694', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498518', '6353', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498522', '1498', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498523', '5789', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498118', '268498544', '8542', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1773970090084');
INSERT INTO `items` VALUES ('268498467', '268498545', '1889', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773113316899');
INSERT INTO `items` VALUES ('268498467', '268498551', '1885', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773113348789');
INSERT INTO `items` VALUES ('268498467', '268498552', '2978', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773113349588');
INSERT INTO `items` VALUES ('268494356', '268498558', '3470', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268498074', '268498568', '1864', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126138958');
INSERT INTO `items` VALUES ('268498074', '268498592', '1799', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126314868');
INSERT INTO `items` VALUES ('268493707', '268498594', '1831', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881745968');
INSERT INTO `items` VALUES ('268493707', '268498595', '1869', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881745968');
INSERT INTO `items` VALUES ('268497182', '268498603', '1872', '88', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268497182', '268498604', '1868', '45', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268497182', '268498606', '1864', '21', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268497182', '268498607', '1865', '19', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268493707', '268498608', '1864', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881839218');
INSERT INTO `items` VALUES ('268493707', '268498609', '1866', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1771881839219');
INSERT INTO `items` VALUES ('268497182', '268498610', '1873', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268493309', '268498634', '6392', '670', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268498467', '268498644', '1864', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498645', '736', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498646', '1869', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498651', '17', '23', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268493856', '268498652', '5482', '2', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498653', '5479', '2', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268493856', '268498654', '5478', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461770');
INSERT INTO `items` VALUES ('268494667', '268498659', '871', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430326');
INSERT INTO `items` VALUES ('268493309', '268498660', '1458', '94', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268494667', '268498661', '893', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430329');
INSERT INTO `items` VALUES ('268494667', '268498662', '924', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430328');
INSERT INTO `items` VALUES ('268494667', '268498663', '871', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430324');
INSERT INTO `items` VALUES ('268494667', '268498664', '893', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430331');
INSERT INTO `items` VALUES ('268494667', '268498665', '6383', '1', '2', 'INVENTORY', '0', '0', '0', '-1', '1777423430337');
INSERT INTO `items` VALUES ('268494667', '268498666', '6384', '1', '2', 'INVENTORY', '0', '0', '0', '-1', '1777423430336');
INSERT INTO `items` VALUES ('268494667', '268498667', '6385', '1', '2', 'INVENTORY', '0', '0', '0', '-1', '1777423430338');
INSERT INTO `items` VALUES ('268498467', '268498668', '1872', '132', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498669', '1867', '136', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268494667', '268498670', '6386', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430332');
INSERT INTO `items` VALUES ('268494667', '268498671', '6377', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430335');
INSERT INTO `items` VALUES ('268494667', '268498672', '6622', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430349');
INSERT INTO `items` VALUES ('268494667', '268498675', '1538', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430322');
INSERT INTO `items` VALUES ('268498467', '268498677', '1831', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498696', '1868', '59', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268494667', '268498697', '1459', '250', '0', 'INVENTORY', '0', '0', '0', '-1', '1777423430322');
INSERT INTO `items` VALUES ('268498467', '268498698', '1870', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498702', '1871', '28', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498710', '1873', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498712', '6407', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167805');
INSERT INTO `items` VALUES ('268498467', '268498715', '1866', '86', '0', 'INVENTORY', '0', '0', '0', '-1', '1773110167803');
INSERT INTO `items` VALUES ('268494356', '268498729', '1871', '22', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268498730', '1866', '17', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268497182', '268498744', '1466', '20000', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268498745', '1871', '20', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268498746', '1870', '17', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268498747', '1876', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268494667', '268498806', '6392', '388', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430349');
INSERT INTO `items` VALUES ('268494667', '268498807', '1866', '123', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430349');
INSERT INTO `items` VALUES ('268494667', '268498808', '1865', '91', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494228', '268498835', '5535', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273557');
INSERT INTO `items` VALUES ('268498467', '268498866', '2971', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773113941540');
INSERT INTO `items` VALUES ('268493309', '268498890', '1895', '93', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268493309', '268498896', '6669', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268494667', '268498934', '5575', '4610751', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268493876', '268498958', '3470', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268494228', '268499065', '1864', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273556');
INSERT INTO `items` VALUES ('268494228', '268499070', '1869', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1777088273556');
INSERT INTO `items` VALUES ('268494228', '268499258', '1876', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777087654094');
INSERT INTO `items` VALUES ('268494667', '268499293', '1539', '169', '0', 'INVENTORY', '0', '0', '0', '-1', '1777434077437');
INSERT INTO `items` VALUES ('268494751', '268499302', '151', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1779163691665');
INSERT INTO `items` VALUES ('268494751', '268499303', '893', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1779163691661');
INSERT INTO `items` VALUES ('268494751', '268499304', '862', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1779163691657');
INSERT INTO `items` VALUES ('268494751', '268499305', '862', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1779163691654');
INSERT INTO `items` VALUES ('268494751', '268499306', '924', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1779163691659');
INSERT INTO `items` VALUES ('268494751', '268499307', '2407', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1779163691670');
INSERT INTO `items` VALUES ('268494751', '268499308', '512', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1779163691662');
INSERT INTO `items` VALUES ('268494751', '268499309', '5767', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1779163691669');
INSERT INTO `items` VALUES ('268494751', '268499310', '5779', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1779163691671');
INSERT INTO `items` VALUES ('268494751', '268499311', '2498', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1779163691667');
INSERT INTO `items` VALUES ('268494751', '268499312', '3951', '94682', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268498467', '268499385', '1832', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115218917');
INSERT INTO `items` VALUES ('268494356', '268499420', '1865', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268499586', '1879', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494356', '268499612', '1878', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268494184', '268499624', '1872', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382109');
INSERT INTO `items` VALUES ('268498107', '268499650', '3947', '3000', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071781082');
INSERT INTO `items` VALUES ('268498107', '268499651', '1540', '80', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071804378');
INSERT INTO `items` VALUES ('268498107', '268499652', '1538', '25', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071809018');
INSERT INTO `items` VALUES ('268498107', '268499653', '1539', '991', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071824911');
INSERT INTO `items` VALUES ('268498107', '268499654', '728', '956', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071829986');
INSERT INTO `items` VALUES ('268498107', '268499655', '3031', '552', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071838127');
INSERT INTO `items` VALUES ('268498107', '268499656', '1458', '87', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071845367');
INSERT INTO `items` VALUES ('268498107', '268499657', '1459', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1777071849706');
INSERT INTO `items` VALUES ('268498107', '268499778', '1797', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072167288');
INSERT INTO `items` VALUES ('268498467', '268500195', '2191', '17', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115252656');
INSERT INTO `items` VALUES ('268498467', '268500393', '2919', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115556182');
INSERT INTO `items` VALUES ('268498467', '268500394', '2918', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115556182');
INSERT INTO `items` VALUES ('268493876', '268500401', '1801', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569332');
INSERT INTO `items` VALUES ('268498467', '268500414', '5549', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115701380');
INSERT INTO `items` VALUES ('268498467', '268500420', '1877', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115727661');
INSERT INTO `items` VALUES ('268498467', '268500425', '1940', '25', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115748257');
INSERT INTO `items` VALUES ('268494184', '268500464', '2254', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771025382118');
INSERT INTO `items` VALUES ('268493856', '268500468', '1878', '62', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268493856', '268500469', '1867', '726', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268493856', '268500470', '5549', '47', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268493856', '268500471', '1868', '1296', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268493856', '268500472', '1866', '215', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268498467', '268500620', '1902', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1773115963863');
INSERT INTO `items` VALUES ('268498467', '268500626', '1879', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773116015838');
INSERT INTO `items` VALUES ('268498467', '268500630', '1938', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1773116037781');
INSERT INTO `items` VALUES ('268498467', '268500632', '2159', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773116038657');
INSERT INTO `items` VALUES ('268493876', '268500723', '2141', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770474569331');
INSERT INTO `items` VALUES ('268493309', '268501176', '1867', '151', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501177', '2250', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501533', '2985', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501580', '1881', '24', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501581', '737', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501587', '1885', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501737', '5549', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501758', '2976', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268501762', '6667', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268502219', '1901', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898613');
INSERT INTO `items` VALUES ('268493309', '268502410', '1938', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268502457', '1875', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268498074', '268502760', '1539', '961', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126425842');
INSERT INTO `items` VALUES ('268493707', '268502761', '2143', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771882099885');
INSERT INTO `items` VALUES ('268498074', '268502762', '728', '705', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126440489');
INSERT INTO `items` VALUES ('268498074', '268502763', '3031', '137', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126449076');
INSERT INTO `items` VALUES ('268498074', '268502764', '1785', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126452601');
INSERT INTO `items` VALUES ('268498074', '268502765', '1458', '174', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126455913');
INSERT INTO `items` VALUES ('268498074', '268502766', '1459', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1776126458831');
INSERT INTO `items` VALUES ('268494356', '268502769', '1868', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268498118', '268502903', '1869', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1773970759988');
INSERT INTO `items` VALUES ('268498118', '268502904', '1864', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1773970759989');
INSERT INTO `items` VALUES ('268493856', '268502931', '4043', '2', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268493707', '268502958', '1871', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1771882362681');
INSERT INTO `items` VALUES ('268498074', '268502987', '280', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1776126989644');
INSERT INTO `items` VALUES ('268498074', '268502988', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1776126998439');
INSERT INTO `items` VALUES ('268498074', '268502989', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1776126999612');
INSERT INTO `items` VALUES ('268498074', '268502990', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1776127002691');
INSERT INTO `items` VALUES ('268498074', '268502991', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1776127003640');
INSERT INTO `items` VALUES ('268498074', '268502992', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1776127005904');
INSERT INTO `items` VALUES ('268494667', '268503063', '2134', '3', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268498118', '268503092', '1870', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1773970852861');
INSERT INTO `items` VALUES ('268498118', '268503093', '1865', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1773970852861');
INSERT INTO `items` VALUES ('268493856', '268503099', '5535', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863491337');
INSERT INTO `items` VALUES ('268498074', '268503129', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1776127028139');
INSERT INTO `items` VALUES ('268498074', '268503130', '2448', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1776127031011');
INSERT INTO `items` VALUES ('268498074', '268503131', '395', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1776127033020');
INSERT INTO `items` VALUES ('268498074', '268503132', '417', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1776127035222');
INSERT INTO `items` VALUES ('268498074', '268503133', '2426', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1776127037425');
INSERT INTO `items` VALUES ('268498074', '268503134', '1341', '50000', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1776127065555');
INSERT INTO `items` VALUES ('268498074', '268503135', '1463', '50000', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127086961');
INSERT INTO `items` VALUES ('268493856', '268503155', '5536', '1', '0', 'CLANWH', '0', '0', '0', '-1', '1777863461771');
INSERT INTO `items` VALUES ('268498074', '268503284', '1878', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127215904');
INSERT INTO `items` VALUES ('268494667', '268503310', '6608', '1', '2', 'INVENTORY', '0', '0', '0', '-1', '1777423430333');
INSERT INTO `items` VALUES ('268498074', '268503322', '1872', '54', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127275027');
INSERT INTO `items` VALUES ('268498074', '268503323', '1867', '52', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127275027');
INSERT INTO `items` VALUES ('268498074', '268503326', '1869', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127297367');
INSERT INTO `items` VALUES ('268498118', '268503332', '1831', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971134331');
INSERT INTO `items` VALUES ('268498118', '268503333', '1871', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971140542');
INSERT INTO `items` VALUES ('268498118', '268503334', '1866', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971140542');
INSERT INTO `items` VALUES ('268493309', '268503431', '5592', '24991', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268494667', '268503542', '6392', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1777431655227');
INSERT INTO `items` VALUES ('268497182', '268503587', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503588', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268493309', '268503617', '5589', '4316', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268503626', '1873', '140', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268503627', '4044', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493707', '268503672', '6037', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771883302659');
INSERT INTO `items` VALUES ('268493707', '268503693', '1463', '15821', '0', 'INVENTORY', '0', '0', '0', '-1', '1771883452612');
INSERT INTO `items` VALUES ('268493707', '268503695', '2499', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1771883461153');
INSERT INTO `items` VALUES ('268493707', '268503696', '2493', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1771883465456');
INSERT INTO `items` VALUES ('268493707', '268503697', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1771883468090');
INSERT INTO `items` VALUES ('268493707', '268503698', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1771883470082');
INSERT INTO `items` VALUES ('268493707', '268503699', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1771883472030');
INSERT INTO `items` VALUES ('268493707', '268503700', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1771883474091');
INSERT INTO `items` VALUES ('268493707', '268503701', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1771883476041');
INSERT INTO `items` VALUES ('268493707', '268503702', '6666', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1771883533312');
INSERT INTO `items` VALUES ('268498074', '268503726', '6360', '9397', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127616290');
INSERT INTO `items` VALUES ('268498074', '268503727', '6362', '3186', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127616290');
INSERT INTO `items` VALUES ('268498074', '268503728', '2176', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127616291');
INSERT INTO `items` VALUES ('268498074', '268503729', '1868', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127616291');
INSERT INTO `items` VALUES ('268498118', '268503737', '280', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971454093');
INSERT INTO `items` VALUES ('268498118', '268503739', '1341', '5000', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971466896');
INSERT INTO `items` VALUES ('268498118', '268503740', '1539', '900', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971479450');
INSERT INTO `items` VALUES ('268498118', '268503741', '728', '900', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971482558');
INSERT INTO `items` VALUES ('268498118', '268503742', '3031', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971488360');
INSERT INTO `items` VALUES ('268498118', '268503743', '1458', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971495595');
INSERT INTO `items` VALUES ('268498118', '268503744', '1459', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971499025');
INSERT INTO `items` VALUES ('268503745', '268503746', '425', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971581001');
INSERT INTO `items` VALUES ('268503745', '268503747', '461', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971581002');
INSERT INTO `items` VALUES ('268503745', '268503748', '6', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971581000');
INSERT INTO `items` VALUES ('268503745', '268503749', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578655');
INSERT INTO `items` VALUES ('268503745', '268503750', '4037', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971580998');
INSERT INTO `items` VALUES ('268503745', '268503753', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578660');
INSERT INTO `items` VALUES ('268503745', '268503754', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578660');
INSERT INTO `items` VALUES ('268503745', '268503755', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578660');
INSERT INTO `items` VALUES ('268503745', '268503756', '15018', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578660');
INSERT INTO `items` VALUES ('268503745', '268503758', '15023', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578660');
INSERT INTO `items` VALUES ('268503745', '268503759', '15024', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578660');
INSERT INTO `items` VALUES ('268503745', '268503760', '15025', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971578660');
INSERT INTO `items` VALUES ('268497182', '268503763', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503764', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268498074', '268503777', '1871', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127634614');
INSERT INTO `items` VALUES ('268497182', '268503787', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503788', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503789', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503790', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503791', '6577', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503831', '6578', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503832', '6578', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503833', '6578', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503834', '6578', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433852');
INSERT INTO `items` VALUES ('268497182', '268503835', '6578', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779162433851');
INSERT INTO `items` VALUES ('268498074', '268503839', '1865', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127691421');
INSERT INTO `items` VALUES ('268498074', '268503840', '1870', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127691421');
INSERT INTO `items` VALUES ('268498074', '268503868', '6037', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127699319');
INSERT INTO `items` VALUES ('268498074', '268503869', '1873', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127728712');
INSERT INTO `items` VALUES ('268498107', '268503875', '1869', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072247223');
INSERT INTO `items` VALUES ('268498107', '268503876', '1864', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072247223');
INSERT INTO `items` VALUES ('268493707', '268503879', '1932', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771883574666');
INSERT INTO `items` VALUES ('268498074', '268503880', '734', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127782309');
INSERT INTO `items` VALUES ('268503745', '268503895', '57', '47868131', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971612798');
INSERT INTO `items` VALUES ('268503745', '268503899', '5790', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971636245');
INSERT INTO `items` VALUES ('268498107', '268503958', '1870', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072367222');
INSERT INTO `items` VALUES ('268498107', '268503959', '1865', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072367222');
INSERT INTO `items` VALUES ('268498107', '268503960', '6360', '880', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072382021');
INSERT INTO `items` VALUES ('268498107', '268503961', '6362', '274', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072382022');
INSERT INTO `items` VALUES ('268498107', '268503962', '1868', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072382022');
INSERT INTO `items` VALUES ('268498107', '268503963', '1873', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072382022');
INSERT INTO `items` VALUES ('268498107', '268503964', '2176', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072401623');
INSERT INTO `items` VALUES ('268503745', '268503984', '1868', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971761129');
INSERT INTO `items` VALUES ('268503745', '268503985', '17', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971761129');
INSERT INTO `items` VALUES ('268498074', '268503992', '1866', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127834310');
INSERT INTO `items` VALUES ('268498107', '268503999', '1867', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072504019');
INSERT INTO `items` VALUES ('268498074', '268504035', '1877', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127910691');
INSERT INTO `items` VALUES ('268498074', '268504036', '2182', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127910691');
INSERT INTO `items` VALUES ('268498107', '268504038', '6037', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072536021');
INSERT INTO `items` VALUES ('268503745', '268504067', '3470', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971884561');
INSERT INTO `items` VALUES ('268503745', '268504068', '7694', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1773971884561');
INSERT INTO `items` VALUES ('268493707', '268504073', '5220', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771883845479');
INSERT INTO `items` VALUES ('268498074', '268504085', '1930', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776127913112');
INSERT INTO `items` VALUES ('268503745', '268504100', '177', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504101', '1101', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504102', '1104', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504103', '908', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504104', '115', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504105', '115', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504106', '877', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504107', '877', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268503745', '268504109', '3948', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971887836');
INSERT INTO `items` VALUES ('268498107', '268504114', '1928', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072598021');
INSERT INTO `items` VALUES ('268498107', '268504115', '1926', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072598021');
INSERT INTO `items` VALUES ('268498107', '268504157', '1878', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072756821');
INSERT INTO `items` VALUES ('268498107', '268504158', '1884', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072756821');
INSERT INTO `items` VALUES ('268498107', '268504159', '1874', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777072756821');
INSERT INTO `items` VALUES ('268494667', '268504179', '3470', '5', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504180', '1873', '94', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504181', '1895', '36', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504182', '1868', '459', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504183', '1876', '29', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504184', '1864', '322', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504185', '1344', '9117', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504186', '1882', '18', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504189', '5538', '2', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504192', '4040', '4', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268503745', '268504198', '1864', '84', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971933028');
INSERT INTO `items` VALUES ('268503745', '268504199', '1869', '46', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971933028');
INSERT INTO `items` VALUES ('268494356', '268504209', '734', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268503745', '268504212', '1865', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971989025');
INSERT INTO `items` VALUES ('268503745', '268504213', '1870', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1773971989025');
INSERT INTO `items` VALUES ('268498074', '268504241', '1882', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1776128140618');
INSERT INTO `items` VALUES ('268498074', '268504242', '735', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1776128140618');
INSERT INTO `items` VALUES ('268493707', '268504311', '6667', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1771884158248');
INSERT INTO `items` VALUES ('268494667', '268504346', '5541', '2', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504373', '1877', '3', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268498107', '268504377', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777073226851');
INSERT INTO `items` VALUES ('268498107', '268504378', '6392', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1777073241449');
INSERT INTO `items` VALUES ('268504379', '268504383', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268498467', '268504387', '2989', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773119078517');
INSERT INTO `items` VALUES ('268494667', '268504390', '4042', '7', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504403', '5550', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504404', '1880', '8', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268504405', '1869', '89', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268504379', '268504411', '57', '23377327', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504414', '1069', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268493707', '268504426', '605', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771884413079');
INSERT INTO `items` VALUES ('268493707', '268504427', '1123', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771884413079');
INSERT INTO `items` VALUES ('268493707', '268504429', '2153', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771884415571');
INSERT INTO `items` VALUES ('268504379', '268504432', '1463', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504497', '3470', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504499', '8542', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268494667', '268504556', '1894', '5', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268503745', '268504563', '1930', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1773972297830');
INSERT INTO `items` VALUES ('268493707', '268504604', '1885', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771884590267');
INSERT INTO `items` VALUES ('268503745', '268504654', '6666', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1773972413428');
INSERT INTO `items` VALUES ('268493467', '268504673', '4', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151036');
INSERT INTO `items` VALUES ('268504379', '268504732', '7570', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504733', '7559', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504795', '1797', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504796', '1864', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504812', '1869', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504842', '1868', '25', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504843', '1873', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504844', '2178', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504845', '5549', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504887', '1865', '17', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504888', '1870', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504974', '1540', '192', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504976', '1538', '24', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504977', '1539', '896', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504978', '728', '852', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504979', '3031', '190', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504980', '1785', '150', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352167');
INSERT INTO `items` VALUES ('268504379', '268504981', '1458', '147', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504982', '1459', '150', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504984', '8563', '1', '0', 'PAPERDOLL', '16', '0', '0', '-1', '1777078352180');
INSERT INTO `items` VALUES ('268504379', '268504987', '1872', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504988', '1867', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504992', '1882', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268504993', '6666', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268503745', '268505006', '1880', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1773972652019');
INSERT INTO `items` VALUES ('268503745', '268505011', '625', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773972712421');
INSERT INTO `items` VALUES ('268503745', '268505012', '1148', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773972712421');
INSERT INTO `items` VALUES ('268503745', '268505060', '2176', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1773972785219');
INSERT INTO `items` VALUES ('268493707', '268505066', '1341', '30', '0', 'INVENTORY', '0', '0', '0', '-1', '1771885285067');
INSERT INTO `items` VALUES ('268504379', '268505134', '5592', '20', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268505143', '1875', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268505145', '1879', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268494751', '268505148', '728', '1141', '0', 'INVENTORY', '0', '0', '0', '-1', '1779163691647');
INSERT INTO `items` VALUES ('268493707', '268505270', '1832', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771885479892');
INSERT INTO `items` VALUES ('268503745', '268505293', '1801', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773973080818');
INSERT INTO `items` VALUES ('268504379', '268505378', '1876', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268505379', '1880', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268505380', '6667', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268498467', '268505393', '2980', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773119661438');
INSERT INTO `items` VALUES ('268498467', '268505394', '2975', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773119661438');
INSERT INTO `items` VALUES ('268493707', '268505481', '1939', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771885851101');
INSERT INTO `items` VALUES ('268494667', '268505530', '1870', '44', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268505631', '6344', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268505632', '6346', '2', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268493707', '268505636', '1878', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771885972265');
INSERT INTO `items` VALUES ('268493707', '268505699', '737', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771886157476');
INSERT INTO `items` VALUES ('268493707', '268505706', '1874', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771886191079');
INSERT INTO `items` VALUES ('268493707', '268505707', '1884', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771886191079');
INSERT INTO `items` VALUES ('268503745', '268505785', '1817', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773973422359');
INSERT INTO `items` VALUES ('268498074', '268506040', '1928', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776129732174');
INSERT INTO `items` VALUES ('268498074', '268506041', '1926', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1776129732174');
INSERT INTO `items` VALUES ('268493707', '268506125', '2185', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771886665075');
INSERT INTO `items` VALUES ('268493707', '268506139', '3470', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771886723067');
INSERT INTO `items` VALUES ('268504379', '268506165', '1878', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268506166', '1884', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268506175', '1881', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268494667', '268506228', '6672', '3', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268506235', '4044', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268493707', '268506334', '1882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771886880664');
INSERT INTO `items` VALUES ('268504379', '268506379', '1885', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268503745', '268506410', '877', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773974003958');
INSERT INTO `items` VALUES ('268503745', '268506411', '1897', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773974003958');
INSERT INTO `items` VALUES ('268494667', '268506476', '6671', '2', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268506494', '7670', '1', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268506513', '1885', '7', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268504379', '268506635', '1866', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268506636', '1871', '25', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268506697', '2181', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268495502', '268506794', '2010', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483921');
INSERT INTO `items` VALUES ('268495502', '268506795', '1333', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483923');
INSERT INTO `items` VALUES ('268504379', '268506851', '1877', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268507181', '2176', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268493309', '268507591', '6037', '46', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268494667', '268507599', '1538', '20', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268507600', '3952', '4999', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268507601', '1539', '811', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268494667', '268507602', '728', '835', '0', 'WAREHOUSE', '0', '0', '0', '-1', '1777423430350');
INSERT INTO `items` VALUES ('268504379', '268508034', '2508', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268508051', '84', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1777078352173');
INSERT INTO `items` VALUES ('268504379', '268508052', '855', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1777078352168');
INSERT INTO `items` VALUES ('268504379', '268508053', '855', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1777078352169');
INSERT INTO `items` VALUES ('268504379', '268508054', '885', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1777078352171');
INSERT INTO `items` VALUES ('268504379', '268508055', '885', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1777078352171');
INSERT INTO `items` VALUES ('268504379', '268508056', '119', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1777078352170');
INSERT INTO `items` VALUES ('268504379', '268508057', '439', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1777078352177');
INSERT INTO `items` VALUES ('268504379', '268508058', '471', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1777078352178');
INSERT INTO `items` VALUES ('268504379', '268508059', '2454', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1777078352175');
INSERT INTO `items` VALUES ('268504379', '268508060', '631', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1777078352174');
INSERT INTO `items` VALUES ('268504379', '268508061', '2414', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1777078352172');
INSERT INTO `items` VALUES ('268504379', '268508062', '603', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1777078352179');
INSERT INTO `items` VALUES ('268504379', '268508063', '3949', '5000', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268508235', '4037', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268508236', '1341', '190', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268508237', '6668', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268504379', '268508262', '2945', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777078352166');
INSERT INTO `items` VALUES ('268523291', '268509488', '3031', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664758823');
INSERT INTO `items` VALUES ('268509776', '268509777', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268509778', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062428');
INSERT INTO `items` VALUES ('268509776', '268509779', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062427');
INSERT INTO `items` VALUES ('268509776', '268509780', '2370', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062426');
INSERT INTO `items` VALUES ('268509776', '268509781', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062425');
INSERT INTO `items` VALUES ('268509776', '268509782', '4037', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062425');
INSERT INTO `items` VALUES ('268494356', '268509800', '1898', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1777429606445');
INSERT INTO `items` VALUES ('268509776', '268509815', '57', '77136742', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062425');
INSERT INTO `items` VALUES ('268509776', '268509992', '5789', '253', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062425');
INSERT INTO `items` VALUES ('268493467', '268510089', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151037');
INSERT INTO `items` VALUES ('268509776', '268510096', '3031', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268509776', '268510097', '728', '385', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268510098', '1539', '771', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268510099', '1463', '41474', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062431');
INSERT INTO `items` VALUES ('268493467', '268510103', '1786', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151037');
INSERT INTO `items` VALUES ('268509776', '268510211', '7817', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062431');
INSERT INTO `items` VALUES ('268509776', '268510344', '1868', '228', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062430');
INSERT INTO `items` VALUES ('268509776', '268510347', '1831', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268510353', '1869', '78', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268510361', '1872', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062429');
INSERT INTO `items` VALUES ('268509776', '268510514', '159', '1', '3', 'PAPERDOLL', '7', '0', '0', '-1', '1770697062439');
INSERT INTO `items` VALUES ('268509776', '268510515', '882', '1', '3', 'PAPERDOLL', '5', '0', '0', '-1', '1770697062437');
INSERT INTO `items` VALUES ('268509776', '268510516', '882', '1', '3', 'PAPERDOLL', '4', '0', '0', '-1', '1770697062436');
INSERT INTO `items` VALUES ('268509776', '268510517', '851', '1', '3', 'PAPERDOLL', '2', '0', '0', '-1', '1770697062434');
INSERT INTO `items` VALUES ('268509776', '268510518', '851', '1', '3', 'PAPERDOLL', '1', '0', '0', '-1', '1770697062432');
INSERT INTO `items` VALUES ('268509776', '268510519', '914', '1', '3', 'PAPERDOLL', '3', '0', '0', '-1', '1770697062435');
INSERT INTO `items` VALUES ('268509776', '268510520', '2493', '1', '3', 'PAPERDOLL', '8', '0', '0', '-1', '1770697062440');
INSERT INTO `items` VALUES ('268509776', '268510521', '2425', '1', '3', 'PAPERDOLL', '13', '0', '0', '-1', '1770697062445');
INSERT INTO `items` VALUES ('268509776', '268510522', '2449', '1', '3', 'PAPERDOLL', '10', '0', '0', '-1', '1770697062441');
INSERT INTO `items` VALUES ('268509776', '268510523', '2378', '1', '3', 'PAPERDOLL', '12', '0', '0', '-1', '1770697062444');
INSERT INTO `items` VALUES ('268509776', '268510524', '352', '1', '3', 'PAPERDOLL', '11', '0', '0', '-1', '1770697062442');
INSERT INTO `items` VALUES ('268509776', '268510525', '2411', '1', '3', 'PAPERDOLL', '6', '0', '0', '-1', '1770697062438');
INSERT INTO `items` VALUES ('268503745', '268510820', '1873', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1773977928757');
INSERT INTO `items` VALUES ('268493467', '268510901', '21', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151038');
INSERT INTO `items` VALUES ('268493467', '268510902', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151041');
INSERT INTO `items` VALUES ('268509776', '268511314', '6360', '6219', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511315', '6362', '2816', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511316', '1873', '33', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511317', '2176', '14', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511318', '6037', '27', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511319', '1871', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062431');
INSERT INTO `items` VALUES ('268509776', '268511337', '1884', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511338', '1878', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511351', '1865', '20', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511352', '1870', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511353', '1879', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511407', '1930', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511408', '39', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268511412', '1928', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062419');
INSERT INTO `items` VALUES ('268493467', '268512765', '21', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151043');
INSERT INTO `items` VALUES ('268493467', '268512766', '28', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151044');
INSERT INTO `items` VALUES ('268493467', '268512783', '7', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151045');
INSERT INTO `items` VALUES ('268493467', '268512784', '4', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151046');
INSERT INTO `items` VALUES ('268497394', '268512830', '3470', '21', '0', 'INVENTORY', '0', '0', '0', '-1', '1777444096760');
INSERT INTO `items` VALUES ('268493467', '268512967', '7', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151048');
INSERT INTO `items` VALUES ('268493467', '268512968', '4', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1769384151031');
INSERT INTO `items` VALUES ('268509776', '268513880', '1925', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268513881', '1866', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268513882', '1897', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268513883', '734', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268509776', '268513884', '1898', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268495502', '268514107', '2005', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483923');
INSERT INTO `items` VALUES ('268509776', '268514237', '2151', '6', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268503745', '268514246', '1878', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773980900493');
INSERT INTO `items` VALUES ('268503745', '268514247', '2151', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773980900493');
INSERT INTO `items` VALUES ('268509776', '268514289', '1804', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268509776', '268514318', '1926', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268509776', '268514319', '34', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268509776', '268514320', '6035', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062424');
INSERT INTO `items` VALUES ('268495502', '268514333', '1666', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483923');
INSERT INTO `items` VALUES ('268509776', '268514538', '6392', '15', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268514549', '6622', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268514885', '6361', '2144', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268514886', '1876', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268514887', '1895', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268514888', '5549', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268514890', '3002', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062421');
INSERT INTO `items` VALUES ('268509776', '268514891', '1952', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770697062419');
INSERT INTO `items` VALUES ('268498467', '268516404', '2984', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1773129387026');
INSERT INTO `items` VALUES ('268503745', '268517304', '1879', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773982772644');
INSERT INTO `items` VALUES ('268503745', '268518093', '2178', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773983136643');
INSERT INTO `items` VALUES ('268494406', '268518190', '1539', '700', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100490');
INSERT INTO `items` VALUES ('268494406', '268518191', '2508', '800000', '0', 'INVENTORY', '0', '0', '0', '-1', '1770950100488');
INSERT INTO `items` VALUES ('268498467', '268521041', '2158', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773136253038');
INSERT INTO `items` VALUES ('268498467', '268522877', '2288', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1773138954576');
INSERT INTO `items` VALUES ('268523291', '268523292', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172559');
INSERT INTO `items` VALUES ('268523291', '268523293', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172560');
INSERT INTO `items` VALUES ('268523291', '268523294', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172561');
INSERT INTO `items` VALUES ('268523291', '268523295', '2370', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172562');
INSERT INTO `items` VALUES ('268523291', '268523296', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172562');
INSERT INTO `items` VALUES ('268523291', '268523430', '57', '25217621', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172556');
INSERT INTO `items` VALUES ('268523291', '268523436', '6353', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172555');
INSERT INTO `items` VALUES ('268523291', '268524322', '1833', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268524420', '1865', '38', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268524427', '1921', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268524433', '1871', '10', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268524540', '1803', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268524541', '1864', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268524543', '1793', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268524736', '20', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172555');
INSERT INTO `items` VALUES ('268523291', '268524754', '20', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172563');
INSERT INTO `items` VALUES ('268523291', '268524784', '1866', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172563');
INSERT INTO `items` VALUES ('268523291', '268524977', '736', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172568');
INSERT INTO `items` VALUES ('268523291', '268526285', '1868', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172568');
INSERT INTO `items` VALUES ('268523291', '268526358', '1896', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172568');
INSERT INTO `items` VALUES ('268523291', '268526561', '37', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172568');
INSERT INTO `items` VALUES ('268523291', '268526562', '37', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1770664172586');
INSERT INTO `items` VALUES ('268523291', '268527769', '159', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1770664172577');
INSERT INTO `items` VALUES ('268523291', '268527770', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1770664172575');
INSERT INTO `items` VALUES ('268523291', '268527771', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1770664172581');
INSERT INTO `items` VALUES ('268523291', '268527772', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1770664172582');
INSERT INTO `items` VALUES ('268523291', '268527773', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1770664172580');
INSERT INTO `items` VALUES ('268523291', '268527774', '2425', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172565');
INSERT INTO `items` VALUES ('268523291', '268527775', '2493', '1', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1770664172578');
INSERT INTO `items` VALUES ('268523291', '268527776', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1770664172573');
INSERT INTO `items` VALUES ('268523291', '268527777', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1770664172572');
INSERT INTO `items` VALUES ('268523291', '268527778', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1770664172570');
INSERT INTO `items` VALUES ('268523291', '268527779', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1770664172569');
INSERT INTO `items` VALUES ('268523291', '268527780', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1770664172571');
INSERT INTO `items` VALUES ('268523291', '268527800', '2256', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268527801', '2176', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268527961', '1463', '51315', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268527964', '1869', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268527966', '1898', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528227', '1539', '582', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528228', '728', '949', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528465', '1872', '85', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528466', '2154', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528525', '1870', '43', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528786', '1884', '16', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172565');
INSERT INTO `items` VALUES ('268523291', '268528787', '1878', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528788', '1881', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528804', '1877', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528833', '737', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268528991', '2975', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268529005', '1341', '1377', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268529007', '6667', '23', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268529022', '415', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172550');
INSERT INTO `items` VALUES ('268523291', '268529023', '1938', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172542');
INSERT INTO `items` VALUES ('268523291', '268529024', '1939', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268530031', '1901', '22', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268530040', '5549', '5', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268530050', '1834', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268530322', '1879', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268530412', '2987', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268530823', '1880', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268531019', '1885', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268531020', '2976', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268531080', '3470', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268531374', '1933', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268532160', '1876', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268532836', '5550', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268532849', '2198', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268533087', '1889', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268533137', '2985', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268533530', '1882', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268534283', '5220', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268534595', '2155', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268535124', '2183', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172551');
INSERT INTO `items` VALUES ('268523291', '268535810', '1874', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1770664172542');
INSERT INTO `items` VALUES ('268495502', '268536554', '66', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483925');
INSERT INTO `items` VALUES ('268493605', '268541522', '1341', '33', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268541542', '285', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1771900735316');
INSERT INTO `items` VALUES ('268493605', '268541543', '400', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1771900735318');
INSERT INTO `items` VALUES ('268493605', '268541544', '420', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1771900735320');
INSERT INTO `items` VALUES ('268493605', '268541545', '2460', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1771900735317');
INSERT INTO `items` VALUES ('268493605', '268541546', '2436', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1771900735321');
INSERT INTO `items` VALUES ('268493605', '268541547', '2414', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1771900735314');
INSERT INTO `items` VALUES ('268493605', '268541548', '886', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1771900735312');
INSERT INTO `items` VALUES ('268493605', '268541568', '886', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1771900735313');
INSERT INTO `items` VALUES ('268493605', '268541569', '855', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1771900735308');
INSERT INTO `items` VALUES ('268493605', '268541570', '855', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1771900735309');
INSERT INTO `items` VALUES ('268493605', '268541571', '119', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1771900735311');
INSERT INTO `items` VALUES ('268493605', '268541746', '3470', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268541747', '7694', '1', '0', 'PAPERDOLL', '15', '0', '0', '-1', '1771900735322');
INSERT INTO `items` VALUES ('268493605', '268541766', '1464', '47008', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542335', '1342', '99788', '0', 'PAPERDOLL', '8', '0', '0', '-1', '1771900735316');
INSERT INTO `items` VALUES ('268493605', '268542337', '6668', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542338', '3118', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542343', '3118', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542358', '3111', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542359', '3116', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542605', '2047', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542606', '2967', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542608', '1964', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542613', '1832', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542795', '1876', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268542801', '1881', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268543391', '4907', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268543578', '268543579', '425', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736646');
INSERT INTO `items` VALUES ('268543578', '268543580', '461', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736646');
INSERT INTO `items` VALUES ('268543578', '268543581', '6', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736645');
INSERT INTO `items` VALUES ('268543578', '268543582', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736644');
INSERT INTO `items` VALUES ('268543578', '268543583', '4037', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736644');
INSERT INTO `items` VALUES ('268543578', '268543585', '15022', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736644');
INSERT INTO `items` VALUES ('268543578', '268543586', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736644');
INSERT INTO `items` VALUES ('268543578', '268543587', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736644');
INSERT INTO `items` VALUES ('268543578', '268543588', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376736644');
INSERT INTO `items` VALUES ('268543578', '268543661', '57', '48459512', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376758457');
INSERT INTO `items` VALUES ('268543578', '268543766', '68', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543767', '711', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543768', '715', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543769', '908', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543770', '115', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543771', '115', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543772', '877', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543773', '877', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543775', '1463', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376776523');
INSERT INTO `items` VALUES ('268543578', '268543788', '177', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1771376780638');
INSERT INTO `items` VALUES ('268543578', '268543789', '1101', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1771376780638');
INSERT INTO `items` VALUES ('268543578', '268543790', '1104', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1771376780639');
INSERT INTO `items` VALUES ('268543578', '268543791', '908', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1771376780639');
INSERT INTO `items` VALUES ('268543578', '268543792', '115', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1771376780639');
INSERT INTO `items` VALUES ('268543578', '268543793', '115', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1771376780639');
INSERT INTO `items` VALUES ('268543578', '268543794', '877', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1771376780639');
INSERT INTO `items` VALUES ('268543578', '268543795', '877', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1771376780639');
INSERT INTO `items` VALUES ('268543578', '268543797', '3948', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376780639');
INSERT INTO `items` VALUES ('268543578', '268543893', '1070', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376881702');
INSERT INTO `items` VALUES ('268543578', '268543894', '5790', '100', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376881706');
INSERT INTO `items` VALUES ('268543578', '268543967', '1868', '34', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376950452');
INSERT INTO `items` VALUES ('268543578', '268543987', '1872', '34', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376998640');
INSERT INTO `items` VALUES ('268543578', '268543988', '1867', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1771376998640');
INSERT INTO `items` VALUES ('268543578', '268544138', '2142', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377055844');
INSERT INTO `items` VALUES ('268543578', '268544321', '1866', '17', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377250462');
INSERT INTO `items` VALUES ('268543578', '268544322', '1871', '26', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377250462');
INSERT INTO `items` VALUES ('268543578', '268544333', '6360', '949', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377266245');
INSERT INTO `items` VALUES ('268543578', '268544334', '6362', '400', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377266245');
INSERT INTO `items` VALUES ('268543578', '268544335', '2176', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377266245');
INSERT INTO `items` VALUES ('268543578', '268544353', '1878', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377321023');
INSERT INTO `items` VALUES ('268543578', '268544354', '1884', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771377321024');
INSERT INTO `items` VALUES ('268493309', '268572573', '1419', '21', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898610');
INSERT INTO `items` VALUES ('268493309', '268590342', '1872', '59', '0', 'INVENTORY', '0', '0', '0', '-1', '1773010898609');
INSERT INTO `items` VALUES ('268495502', '268605715', '1333', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483926');
INSERT INTO `items` VALUES ('268543578', '268622630', '1870', '17', '0', 'INVENTORY', '0', '0', '0', '-1', '1771439675554');
INSERT INTO `items` VALUES ('268543578', '268622631', '1865', '18', '0', 'INVENTORY', '0', '0', '0', '-1', '1771439675554');
INSERT INTO `items` VALUES ('268543578', '268622663', '1864', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1771439703552');
INSERT INTO `items` VALUES ('268543578', '268622848', '1869', '19', '0', 'INVENTORY', '0', '0', '0', '-1', '1771439865574');
INSERT INTO `items` VALUES ('268543578', '268622918', '6037', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440007557');
INSERT INTO `items` VALUES ('268543578', '268622919', '1873', '13', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440007558');
INSERT INTO `items` VALUES ('268543578', '268623084', '6666', '9', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440061953');
INSERT INTO `items` VALUES ('268543578', '268623203', '2152', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440114754');
INSERT INTO `items` VALUES ('268543578', '268623241', '2182', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440179554');
INSERT INTO `items` VALUES ('268543578', '268623576', '1874', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440480353');
INSERT INTO `items` VALUES ('268543578', '268623824', '728', '621', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440664698');
INSERT INTO `items` VALUES ('268543578', '268623826', '1539', '909', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440669269');
INSERT INTO `items` VALUES ('268543578', '268624057', '2185', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440943160');
INSERT INTO `items` VALUES ('268543578', '268624096', '1881', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440955572');
INSERT INTO `items` VALUES ('268543578', '268624129', '1876', '7', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440980357');
INSERT INTO `items` VALUES ('268543578', '268624158', '2187', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771440993151');
INSERT INTO `items` VALUES ('268543578', '268624786', '1937', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441243953');
INSERT INTO `items` VALUES ('268543578', '268625683', '1875', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441299554');
INSERT INTO `items` VALUES ('268543578', '268625700', '2971', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441360755');
INSERT INTO `items` VALUES ('268543578', '268625943', '4917', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441645553');
INSERT INTO `items` VALUES ('268543578', '268626335', '2932', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441948754');
INSERT INTO `items` VALUES ('268543578', '268626336', '1949', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441948754');
INSERT INTO `items` VALUES ('268543578', '268626337', '1957', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441948754');
INSERT INTO `items` VALUES ('268543578', '268626348', '2073', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771441976352');
INSERT INTO `items` VALUES ('268543578', '268626377', '1912', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771442048840');
INSERT INTO `items` VALUES ('268543578', '268626378', '1911', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771442048840');
INSERT INTO `items` VALUES ('268543578', '268626423', '3470', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771442092352');
INSERT INTO `items` VALUES ('268543578', '268626762', '4042', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771442443845');
INSERT INTO `items` VALUES ('268543578', '268627430', '1880', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771443223854');
INSERT INTO `items` VALUES ('268543578', '268627431', '5220', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771443239052');
INSERT INTO `items` VALUES ('268543578', '268627440', '1895', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771443253051');
INSERT INTO `items` VALUES ('268543578', '268627491', '3014', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771443326655');
INSERT INTO `items` VALUES ('268495502', '268630415', '3', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1779032483927');
INSERT INTO `items` VALUES ('268493605', '268630925', '1875', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631041', '2225', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631089', '1961', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631090', '2935', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631347', '6360', '2640', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631348', '6361', '6002', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631349', '6362', '235', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631351', '6037', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268493605', '268631552', '1895', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771900735303');
INSERT INTO `items` VALUES ('268631868', '268631869', '1147', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547808');
INSERT INTO `items` VALUES ('268631868', '268631870', '1146', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547809');
INSERT INTO `items` VALUES ('268631868', '268631871', '10', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547810');
INSERT INTO `items` VALUES ('268631868', '268631872', '2369', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547811');
INSERT INTO `items` VALUES ('268631868', '268631873', '5588', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547811');
INSERT INTO `items` VALUES ('268631868', '268631876', '15022', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547811');
INSERT INTO `items` VALUES ('268631868', '268631877', '15021', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547811');
INSERT INTO `items` VALUES ('268631868', '268631878', '15020', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547811');
INSERT INTO `items` VALUES ('268631868', '268631879', '15019', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547811');
INSERT INTO `items` VALUES ('268631868', '268631881', '15017', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547807');
INSERT INTO `items` VALUES ('268631868', '268631883', '57', '40622877', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547807');
INSERT INTO `items` VALUES ('268631868', '268631895', '1067', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268631896', '5789', '200', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268631902', '68', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547801');
INSERT INTO `items` VALUES ('268631868', '268631903', '711', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547803');
INSERT INTO `items` VALUES ('268631868', '268631904', '715', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547804');
INSERT INTO `items` VALUES ('268631868', '268631905', '908', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547804');
INSERT INTO `items` VALUES ('268631868', '268631906', '115', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547805');
INSERT INTO `items` VALUES ('268631868', '268631907', '115', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547806');
INSERT INTO `items` VALUES ('268631868', '268631908', '877', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547807');
INSERT INTO `items` VALUES ('268631868', '268631909', '877', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547812');
INSERT INTO `items` VALUES ('268631868', '268631911', '1463', '50004', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547812');
INSERT INTO `items` VALUES ('268631868', '268631955', '1921', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547812');
INSERT INTO `items` VALUES ('268631868', '268632024', '17', '21', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632025', '1864', '199', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632132', '1869', '96', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632133', '1798', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632270', '734', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632271', '1868', '89', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632272', '1866', '38', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632273', '1878', '27', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632274', '1800', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547812');
INSERT INTO `items` VALUES ('268631868', '268632280', '1872', '71', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547812');
INSERT INTO `items` VALUES ('268631868', '268632281', '1867', '66', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547813');
INSERT INTO `items` VALUES ('268631868', '268632437', '1870', '27', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268632439', '1871', '44', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268632571', '2548', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268632573', '2411', '1', '0', 'PAPERDOLL', '6', '0', '0', '-1', '1771905547820');
INSERT INTO `items` VALUES ('268631868', '268632574', '352', '1', '0', 'PAPERDOLL', '11', '0', '0', '-1', '1771905547823');
INSERT INTO `items` VALUES ('268631868', '268632575', '2378', '1', '0', 'PAPERDOLL', '12', '0', '0', '-1', '1771905547824');
INSERT INTO `items` VALUES ('268631868', '268632577', '2449', '1', '0', 'PAPERDOLL', '10', '0', '0', '-1', '1771905547822');
INSERT INTO `items` VALUES ('268631868', '268632578', '2425', '1', '0', 'PAPERDOLL', '13', '0', '0', '-1', '1771905547825');
INSERT INTO `items` VALUES ('268631868', '268632579', '882', '1', '0', 'PAPERDOLL', '5', '0', '0', '-1', '1771905547819');
INSERT INTO `items` VALUES ('268631868', '268632581', '882', '1', '0', 'PAPERDOLL', '4', '0', '0', '-1', '1771905547818');
INSERT INTO `items` VALUES ('268631868', '268632582', '851', '1', '0', 'PAPERDOLL', '2', '0', '0', '-1', '1771905547816');
INSERT INTO `items` VALUES ('268631868', '268632583', '851', '1', '0', 'PAPERDOLL', '1', '0', '0', '-1', '1771905547814');
INSERT INTO `items` VALUES ('268631868', '268632584', '914', '1', '0', 'PAPERDOLL', '3', '0', '0', '-1', '1771905547817');
INSERT INTO `items` VALUES ('268631868', '268632587', '728', '909', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268632588', '1539', '953', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268632827', '1865', '32', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268632828', '1874', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268632831', '5550', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633075', '6667', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633159', '1341', '228', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633188', '1873', '29', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633190', '1834', '4', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633191', '1884', '46', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633197', '1829', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633204', '1832', '17', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633369', '1880', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547798');
INSERT INTO `items` VALUES ('268631868', '268633532', '1882', '12', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547797');
INSERT INTO `items` VALUES ('268631868', '268633540', '4037', '490', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268633660', '2205', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634148', '2599', '1', '0', 'PAPERDOLL', '7', '0', '0', '-1', '1771905547821');
INSERT INTO `items` VALUES ('268631868', '268634167', '1464', '49352', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634343', '5589', '11', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634495', '3072', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634496', '3080', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634572', '6668', '3', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634749', '1342', '28', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634751', '1969', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634752', '1968', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268634856', '1881', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268635159', '4105', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268635160', '4106', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268635182', '1879', '8', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268635210', '5549', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268635782', '4049', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268635783', '4050', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268636036', '4129', '1', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547800');
INSERT INTO `items` VALUES ('268631868', '268636084', '3470', '2', '0', 'INVENTORY', '0', '0', '0', '-1', '1771905547797');

-- ----------------------------
-- Table structure for `items_delayed`
-- ----------------------------
DROP TABLE IF EXISTS `items_delayed`;
CREATE TABLE `items_delayed` (
  `owner_id` int(11) NOT NULL,
  `item_id` smallint(5) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL DEFAULT 1,
  `enchant_level` smallint(5) unsigned NOT NULL DEFAULT 0,
  `payment_status` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `description` varchar(255) DEFAULT NULL,
  KEY `owner_id` (`owner_id`),
  KEY `item_id` (`item_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2157 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- ----------------------------
-- Records of items_delayed
-- ----------------------------

-- ----------------------------
-- Table structure for `items_on_ground`
-- ----------------------------
DROP TABLE IF EXISTS `items_on_ground`;
CREATE TABLE `items_on_ground` (
  `object_id` int(11) NOT NULL DEFAULT 0,
  `item_id` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `enchant_level` int(11) DEFAULT NULL,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  `z` int(11) DEFAULT NULL,
  `time` decimal(20,0) DEFAULT NULL,
  PRIMARY KEY (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of items_on_ground
-- ----------------------------

-- ----------------------------
-- Table structure for `mdt_bets`
-- ----------------------------
DROP TABLE IF EXISTS `mdt_bets`;
CREATE TABLE `mdt_bets` (
  `lane_id` int(1) NOT NULL DEFAULT 0,
  `bet` int(11) DEFAULT 0,
  PRIMARY KEY (`lane_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of mdt_bets
-- ----------------------------
INSERT INTO `mdt_bets` VALUES ('1', '0');
INSERT INTO `mdt_bets` VALUES ('2', '0');
INSERT INTO `mdt_bets` VALUES ('3', '0');
INSERT INTO `mdt_bets` VALUES ('4', '0');
INSERT INTO `mdt_bets` VALUES ('5', '0');
INSERT INTO `mdt_bets` VALUES ('6', '0');
INSERT INTO `mdt_bets` VALUES ('7', '0');
INSERT INTO `mdt_bets` VALUES ('8', '0');

-- ----------------------------
-- Table structure for `mdt_history`
-- ----------------------------
DROP TABLE IF EXISTS `mdt_history`;
CREATE TABLE `mdt_history` (
  `race_id` mediumint(9) NOT NULL DEFAULT 0,
  `first` int(1) DEFAULT 0,
  `second` int(1) DEFAULT 0,
  `odd_rate` double(10,2) DEFAULT 0.00,
  PRIMARY KEY (`race_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of mdt_history
-- ----------------------------
INSERT INTO `mdt_history` VALUES ('1', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('3', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('4', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('5', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('6', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('7', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('8', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('9', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('10', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('11', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('12', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('13', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('14', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('15', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('16', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('17', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('18', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('19', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('20', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('21', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('22', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('23', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('24', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('25', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('26', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('27', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('28', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('29', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('30', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('31', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('32', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('33', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('34', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('35', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('36', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('37', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('38', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('39', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('40', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('41', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('42', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('43', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('44', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('45', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('46', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('47', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('48', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('49', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('50', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('51', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('52', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('53', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('54', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('55', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('56', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('57', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('58', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('59', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('60', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('61', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('62', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('63', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('64', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('65', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('66', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('67', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('68', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('69', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('70', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('71', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('72', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('73', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('74', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('75', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('76', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('77', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('78', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('79', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('80', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('81', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('82', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('83', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('84', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('85', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('86', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('87', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('88', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('89', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('90', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('91', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('92', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('93', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('94', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('95', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('96', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('97', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('98', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('99', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('100', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('101', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('102', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('103', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('104', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('105', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('106', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('107', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('108', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('109', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('110', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('111', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('112', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('113', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('114', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('115', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('116', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('117', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('118', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('119', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('120', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('121', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('122', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('123', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('124', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('125', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('126', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('127', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('128', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('129', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('130', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('131', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('132', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('133', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('134', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('135', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('136', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('137', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('138', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('139', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('140', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('141', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('142', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('143', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('144', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('145', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('146', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('147', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('148', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('149', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('150', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('151', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('152', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('153', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('154', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('155', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('156', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('157', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('158', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('159', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('160', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('161', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('162', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('163', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('164', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('165', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('166', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('167', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('168', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('169', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('170', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('171', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('172', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('173', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('174', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('175', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('176', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('177', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('178', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('179', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('180', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('181', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('182', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('183', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('184', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('185', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('186', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('187', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('188', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('189', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('190', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('191', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('192', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('193', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('194', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('195', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('196', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('197', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('198', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('199', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('200', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('201', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('202', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('203', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('204', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('205', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('206', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('207', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('208', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('209', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('210', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('211', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('212', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('213', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('214', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('215', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('216', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('217', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('218', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('219', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('220', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('221', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('222', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('223', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('224', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('225', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('226', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('227', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('228', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('229', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('230', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('231', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('232', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('233', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('234', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('235', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('236', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('237', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('238', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('239', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('240', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('241', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('242', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('243', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('244', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('245', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('246', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('247', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('248', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('249', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('250', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('251', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('252', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('253', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('254', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('255', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('256', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('257', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('258', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('259', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('260', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('261', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('262', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('263', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('264', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('265', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('266', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('267', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('268', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('269', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('270', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('271', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('272', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('273', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('274', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('275', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('276', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('277', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('278', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('279', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('280', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('281', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('282', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('283', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('284', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('285', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('286', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('287', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('288', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('289', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('290', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('291', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('292', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('293', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('294', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('295', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('296', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('297', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('298', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('299', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('300', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('301', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('302', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('303', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('304', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('305', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('306', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('307', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('308', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('309', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('310', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('311', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('312', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('313', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('314', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('315', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('316', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('317', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('318', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('319', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('320', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('321', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('322', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('323', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('324', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('325', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('326', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('327', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('328', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('329', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('330', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('331', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('332', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('333', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('334', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('335', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('336', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('337', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('338', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('339', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('340', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('341', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('342', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('343', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('344', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('345', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('346', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('347', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('348', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('349', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('350', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('351', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('352', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('353', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('354', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('355', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('356', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('357', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('358', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('359', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('360', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('361', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('362', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('363', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('364', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('365', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('366', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('367', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('368', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('369', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('370', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('371', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('372', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('373', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('374', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('375', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('376', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('377', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('378', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('379', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('380', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('381', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('382', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('383', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('384', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('385', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('386', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('387', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('388', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('389', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('390', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('391', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('392', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('393', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('394', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('395', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('396', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('397', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('398', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('399', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('400', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('401', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('402', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('403', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('404', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('405', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('406', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('407', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('408', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('409', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('410', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('411', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('412', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('413', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('414', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('415', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('416', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('417', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('418', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('419', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('420', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('421', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('422', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('423', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('424', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('425', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('426', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('427', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('428', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('429', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('430', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('431', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('432', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('433', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('434', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('435', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('436', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('437', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('438', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('439', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('440', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('441', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('442', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('443', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('444', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('445', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('446', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('447', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('448', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('449', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('450', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('451', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('452', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('453', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('454', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('455', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('456', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('457', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('458', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('459', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('460', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('461', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('462', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('463', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('464', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('465', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('466', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('467', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('468', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('469', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('470', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('471', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('472', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('473', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('474', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('475', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('476', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('477', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('478', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('479', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('480', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('481', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('482', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('483', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('484', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('485', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('486', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('487', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('488', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('489', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('490', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('491', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('492', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('493', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('494', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('495', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('496', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('497', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('498', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('499', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('500', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('501', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('502', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('503', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('504', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('505', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('506', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('507', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('508', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('509', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('510', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('511', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('512', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('513', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('514', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('515', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('516', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('517', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('518', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('519', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('520', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('521', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('522', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('523', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('524', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('525', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('526', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('527', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('528', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('529', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('530', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('531', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('532', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('533', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('534', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('535', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('536', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('537', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('538', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('539', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('540', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('541', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('542', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('543', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('544', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('545', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('546', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('547', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('548', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('549', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('550', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('551', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('552', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('553', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('554', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('555', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('556', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('557', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('558', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('559', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('560', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('561', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('562', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('563', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('564', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('565', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('566', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('567', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('568', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('569', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('570', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('571', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('572', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('573', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('574', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('575', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('576', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('577', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('578', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('579', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('580', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('581', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('582', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('583', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('584', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('585', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('586', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('587', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('588', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('589', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('590', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('591', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('592', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('593', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('594', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('595', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('596', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('597', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('598', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('599', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('600', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('601', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('602', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('603', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('604', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('605', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('606', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('607', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('608', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('609', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('610', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('611', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('612', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('613', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('614', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('615', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('616', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('617', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('618', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('619', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('620', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('621', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('622', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('623', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('624', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('625', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('626', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('627', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('628', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('629', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('630', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('631', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('632', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('633', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('634', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('635', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('636', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('637', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('638', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('639', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('640', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('641', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('642', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('643', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('644', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('645', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('646', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('647', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('648', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('649', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('650', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('651', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('652', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('653', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('654', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('655', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('656', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('657', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('658', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('659', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('660', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('661', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('662', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('663', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('664', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('665', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('666', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('667', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('668', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('669', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('670', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('671', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('672', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('673', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('674', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('675', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('676', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('677', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('678', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('679', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('680', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('681', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('682', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('683', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('684', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('685', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('686', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('687', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('688', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('689', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('690', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('691', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('692', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('693', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('694', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('695', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('696', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('697', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('698', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('699', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('700', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('701', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('702', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('703', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('704', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('705', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('706', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('707', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('708', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('709', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('710', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('711', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('712', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('713', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('714', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('715', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('716', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('717', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('718', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('719', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('720', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('721', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('722', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('723', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('724', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('725', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('726', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('727', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('728', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('729', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('730', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('731', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('732', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('733', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('734', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('735', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('736', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('737', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('738', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('739', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('740', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('741', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('742', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('743', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('744', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('745', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('746', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('747', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('748', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('749', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('750', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('751', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('752', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('753', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('754', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('755', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('756', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('757', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('758', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('759', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('760', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('761', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('762', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('763', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('764', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('765', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('766', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('767', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('768', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('769', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('770', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('771', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('772', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('773', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('774', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('775', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('776', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('777', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('778', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('779', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('780', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('781', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('782', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('783', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('784', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('785', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('786', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('787', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('788', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('789', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('790', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('791', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('792', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('793', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('794', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('795', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('796', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('797', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('798', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('799', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('800', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('801', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('802', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('803', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('804', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('805', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('806', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('807', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('808', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('809', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('810', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('811', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('812', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('813', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('814', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('815', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('816', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('817', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('818', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('819', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('820', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('821', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('822', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('823', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('824', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('825', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('826', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('827', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('828', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('829', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('830', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('831', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('832', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('833', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('834', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('835', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('836', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('837', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('838', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('839', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('840', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('841', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('842', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('843', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('844', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('845', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('846', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('847', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('848', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('849', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('850', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('851', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('852', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('853', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('854', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('855', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('856', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('857', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('858', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('859', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('860', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('861', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('862', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('863', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('864', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('865', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('866', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('867', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('868', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('869', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('870', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('871', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('872', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('873', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('874', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('875', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('876', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('877', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('878', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('879', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('880', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('881', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('882', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('883', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('884', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('885', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('886', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('887', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('888', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('889', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('890', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('891', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('892', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('893', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('894', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('895', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('896', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('897', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('898', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('899', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('900', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('901', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('902', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('903', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('904', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('905', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('906', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('907', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('908', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('909', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('910', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('911', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('912', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('913', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('914', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('915', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('916', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('917', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('918', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('919', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('920', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('921', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('922', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('923', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('924', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('925', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('926', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('927', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('928', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('929', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('930', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('931', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('932', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('933', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('934', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('935', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('936', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('937', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('938', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('939', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('940', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('941', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('942', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('943', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('944', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('945', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('946', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('947', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('948', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('949', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('950', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('951', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('952', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('953', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('954', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('955', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('956', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('957', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('958', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('959', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('960', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('961', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('962', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('963', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('964', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('965', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('966', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('967', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('968', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('969', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('970', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('971', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('972', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('973', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('974', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('975', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('976', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('977', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('978', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('979', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('980', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('981', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('982', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('983', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('984', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('985', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('986', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('987', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('988', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('989', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('990', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('991', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('992', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('993', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('994', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('995', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('996', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('997', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('998', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('999', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1000', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1001', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1002', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1003', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1004', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1005', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1006', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1007', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1008', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1009', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1010', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1011', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1012', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1013', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1014', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1015', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1016', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1017', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1018', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1019', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1020', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1021', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1022', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1023', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1024', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1025', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1026', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1027', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1028', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1029', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1030', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1031', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1032', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1033', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1034', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1035', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1036', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1037', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1038', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1039', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1040', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1041', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1042', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1043', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1044', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1045', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1046', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1047', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1048', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1049', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1050', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1051', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1052', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1053', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1054', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1055', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1056', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1057', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1058', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1059', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1060', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1061', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1062', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1063', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1064', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1065', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1066', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1067', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1068', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1069', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1070', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1071', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1072', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1073', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1074', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1075', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1076', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1077', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1078', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1079', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1080', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1081', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1082', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1083', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1084', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1085', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1086', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1087', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1088', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1089', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1090', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1091', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1092', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1093', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1094', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1095', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1096', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1097', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1098', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1099', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1100', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1101', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1102', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1103', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1104', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1105', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1106', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1107', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1108', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1109', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1110', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1111', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1112', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1113', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1114', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1115', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1116', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1117', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1118', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1119', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1120', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1121', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1122', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1123', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1124', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1125', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1126', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1127', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1128', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1129', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1130', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1131', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1132', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1133', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1134', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1135', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1136', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1137', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1138', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1139', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1140', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1141', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1142', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1143', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1144', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1145', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1146', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1147', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1148', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1149', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1150', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1151', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1152', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1153', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1154', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1155', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1156', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1157', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1158', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1159', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1160', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1161', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1162', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1163', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1164', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1165', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1166', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1167', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1168', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1169', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1170', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1171', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1172', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1173', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1174', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1175', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1176', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1177', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1178', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1179', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1180', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1181', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1182', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1183', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1184', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1185', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1186', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1187', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1188', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1189', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1190', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1191', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1192', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1193', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1194', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1195', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1196', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1197', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1198', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1199', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1200', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1201', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1202', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1203', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1204', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1205', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1206', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1207', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1208', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1209', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1210', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1211', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1212', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1213', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1214', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1215', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1216', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1217', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1218', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1219', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1220', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1221', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1222', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1223', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1224', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1225', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1226', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1227', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1228', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1229', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1230', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1231', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1232', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1233', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1234', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1235', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1236', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1237', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1238', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1239', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1240', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1241', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1242', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1243', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1244', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1245', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1246', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1247', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1248', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1249', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1250', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1251', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1252', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1253', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1254', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1255', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1256', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1257', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1258', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1259', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1260', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1261', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1262', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1263', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1264', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1265', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1266', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1267', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1268', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1269', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1270', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1271', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1272', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1273', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1274', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1275', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1276', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1277', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1278', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1279', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1280', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1281', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1282', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1283', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1284', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1285', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1286', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1287', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1288', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1289', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1290', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1291', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1292', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1293', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1294', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1295', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1296', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1297', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1298', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1299', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1300', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1301', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1302', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1303', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1304', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1305', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1306', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1307', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1308', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1309', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1310', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1311', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1312', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1313', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1314', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1315', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1316', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1317', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1318', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1319', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1320', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1321', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1322', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1323', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1324', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1325', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1326', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1327', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1328', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1329', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1330', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1331', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1332', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1333', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1334', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1335', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1336', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1337', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1338', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1339', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1340', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1341', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1342', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1343', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1344', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1345', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1346', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1347', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1348', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1349', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1350', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1351', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1352', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1353', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1354', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1355', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1356', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1357', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1358', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1359', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1360', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1361', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1362', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1363', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1364', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1365', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1366', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1367', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1368', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1369', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1370', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1371', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1372', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1373', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1374', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1375', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1376', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1377', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1378', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1379', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1380', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1381', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1382', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1383', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1384', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1385', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1386', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1387', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1388', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1389', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1390', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1391', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1392', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1393', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1394', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1395', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1396', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1397', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1398', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1399', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1400', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1401', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1402', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1403', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1404', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1405', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1406', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1407', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1408', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1409', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1410', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1411', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1412', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1413', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1414', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1415', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1416', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1417', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1418', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1419', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1420', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1421', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1422', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1423', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1424', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1425', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1426', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1427', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1428', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1429', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1430', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1431', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1432', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1433', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1434', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1435', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1436', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1437', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1438', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1439', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1440', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1441', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1442', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1443', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1444', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1445', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1446', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1447', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1448', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1449', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1450', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1451', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1452', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1453', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1454', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1455', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1456', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1457', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1458', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1459', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1460', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1461', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1462', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1463', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1464', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1465', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1466', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1467', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1468', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1469', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1470', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1471', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1472', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1473', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1474', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1475', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1476', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1477', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1478', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1479', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1480', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1481', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1482', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1483', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1484', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1485', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1486', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1487', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1488', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1489', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1490', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1491', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1492', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1493', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1494', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1495', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1496', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1497', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1498', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1499', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1500', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1501', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1502', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1503', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1504', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1505', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1506', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1507', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1508', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1509', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1510', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1511', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1512', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1513', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1514', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1515', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1516', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1517', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1518', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1519', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1520', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1521', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1522', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1523', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1524', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1525', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1526', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1527', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1528', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1529', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1530', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1531', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1532', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1533', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1534', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1535', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1536', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1537', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1538', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1539', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1540', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1541', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1542', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1543', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1544', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1545', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1546', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1547', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1548', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1549', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1550', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1551', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1552', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1553', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1554', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1555', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1556', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1557', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1558', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1559', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1560', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1561', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1562', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1563', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1564', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1565', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1566', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1567', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1568', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1569', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1570', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1571', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1572', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1573', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1574', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1575', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1576', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1577', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1578', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1579', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1580', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1581', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1582', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1583', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1584', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1585', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1586', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1587', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1588', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1589', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1590', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1591', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1592', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1593', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1594', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1595', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1596', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1597', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1598', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1599', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1600', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1601', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1602', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1603', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1604', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1605', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1606', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1607', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1608', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1609', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1610', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1611', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1612', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1613', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1614', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1615', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1616', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1617', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1618', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1619', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1620', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1621', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1622', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1623', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1624', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1625', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1626', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1627', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1628', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1629', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1630', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1631', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1632', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1633', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1634', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1635', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1636', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1637', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1638', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1639', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1640', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1641', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1642', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1643', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1644', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1645', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1646', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1647', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1648', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1649', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1650', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1651', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1652', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1653', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1654', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1655', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1656', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1657', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1658', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1659', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1660', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1661', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1662', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1663', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1664', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1665', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1666', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1667', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1668', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1669', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1670', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1671', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1672', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1673', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1674', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1675', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1676', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1677', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1678', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1679', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1680', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1681', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1682', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1683', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1684', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1685', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1686', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1687', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1688', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1689', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1690', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1691', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1692', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1693', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1694', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1695', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1696', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1697', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1698', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1699', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1700', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1701', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1702', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1703', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1704', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1705', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1706', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1707', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1708', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1709', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1710', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1711', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1712', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1713', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1714', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1715', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1716', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1717', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1718', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1719', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1720', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1721', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1722', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1723', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1724', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1725', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1726', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1727', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1728', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1729', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1730', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1731', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1732', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1733', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1734', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1735', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1736', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1737', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1738', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1739', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1740', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1741', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1742', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1743', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1744', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1745', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1746', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1747', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1748', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1749', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1750', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1751', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1752', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1753', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1754', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1755', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1756', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1757', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1758', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1759', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1760', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1761', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1762', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1763', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1764', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1765', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1766', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1767', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1768', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1769', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1770', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1771', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1772', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1773', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1774', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1775', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1776', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1777', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1778', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1779', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1780', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1781', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1782', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1783', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1784', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1785', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1786', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1787', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1788', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1789', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1790', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1791', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1792', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1793', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1794', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1795', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1796', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1797', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1798', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1799', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1800', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1801', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1802', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1803', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1804', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1805', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1806', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1807', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1808', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1809', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1810', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1811', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1812', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1813', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1814', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1815', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1816', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1817', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1818', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1819', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1820', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1821', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1822', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1823', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1824', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1825', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1826', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1827', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1828', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1829', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1830', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1831', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1832', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1833', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1834', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1835', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1836', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1837', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1838', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1839', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1840', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1841', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1842', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1843', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1844', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1845', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1846', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1847', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1848', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1849', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1850', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1851', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1852', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1853', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1854', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1855', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1856', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1857', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1858', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1859', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1860', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1861', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1862', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1863', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1864', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1865', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1866', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1867', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1868', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1869', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1870', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1871', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1872', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1873', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1874', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1875', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1876', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1877', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1878', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1879', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1880', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1881', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1882', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1883', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1884', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1885', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1886', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1887', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1888', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1889', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1890', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1891', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1892', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1893', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1894', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1895', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1896', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1897', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1898', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1899', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1900', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1901', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1902', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1903', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1904', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1905', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1906', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1907', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1908', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1909', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1910', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1911', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1912', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1913', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1914', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1915', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1916', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1917', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1918', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1919', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1920', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1921', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1922', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1923', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1924', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1925', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1926', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1927', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1928', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1929', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1930', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1931', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1932', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1933', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1934', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1935', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1936', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1937', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1938', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1939', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1940', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1941', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1942', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1943', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1944', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1945', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1946', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1947', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1948', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1949', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1950', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1951', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1952', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1953', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1954', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1955', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1956', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1957', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1958', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1959', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1960', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1961', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1962', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1963', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1964', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1965', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1966', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1967', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1968', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1969', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1970', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1971', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1972', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1973', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1974', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1975', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1976', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1977', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1978', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1979', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1980', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1981', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1982', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1983', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1984', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('1985', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1986', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('1987', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1988', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('1989', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1990', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1991', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1992', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1993', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1994', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('1995', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('1996', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('1997', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('1998', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('1999', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2000', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2001', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2002', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2003', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2004', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2005', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2006', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2007', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2008', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2009', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2010', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2011', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2012', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2013', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2014', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2015', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2016', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2017', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2018', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2019', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2020', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2021', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2022', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2023', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2024', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2025', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2026', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2027', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2028', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2029', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2030', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2031', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2032', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2033', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2034', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2035', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2036', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2037', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2038', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2039', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2040', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2041', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2042', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2043', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2044', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2045', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2046', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2047', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2048', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2049', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2050', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2051', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2052', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2053', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2054', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2055', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2056', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2057', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2058', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2059', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2060', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2061', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2062', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2063', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2064', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2065', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2066', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2067', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2068', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2069', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2070', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2071', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2072', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2073', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2074', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2075', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2076', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2077', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2078', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2079', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2080', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2081', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2082', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2083', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2084', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2085', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2086', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2087', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2088', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2089', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2090', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2091', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2092', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2093', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2094', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2095', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2096', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2097', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2098', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2099', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2100', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2101', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2102', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2103', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2104', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2105', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2106', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2107', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2108', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2109', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2110', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2111', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2112', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2113', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2114', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2115', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2116', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2117', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2118', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2119', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2120', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2121', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2122', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2123', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2124', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2125', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2126', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2127', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2128', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2129', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2130', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2131', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2132', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2133', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2134', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2135', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2136', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2137', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2138', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2139', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2140', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2141', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2142', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2143', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2144', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2145', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2146', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2147', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2148', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2149', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2150', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2151', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2152', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2153', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2154', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2155', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2156', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2157', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2158', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2159', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2160', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2161', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2162', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2163', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2164', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2165', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2166', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2167', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2168', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2169', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2170', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2171', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2172', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2173', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2174', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2175', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2176', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2177', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2178', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2179', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2180', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2181', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2182', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2183', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2184', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2185', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2186', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2187', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2188', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2189', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2190', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2191', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2192', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2193', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2194', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2195', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2196', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2197', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2198', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2199', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2200', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2201', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2202', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2203', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2204', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2205', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2206', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2207', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2208', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2209', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2210', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2211', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2212', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2213', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2214', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2215', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2216', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2217', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2218', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2219', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2220', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2221', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2222', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2223', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2224', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2225', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2226', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2227', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2228', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2229', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2230', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2231', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2232', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2233', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2234', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2235', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2236', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2237', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2238', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2239', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2240', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2241', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2242', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2243', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2244', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2245', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2246', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2247', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2248', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2249', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2250', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2251', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2252', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2253', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2254', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2255', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2256', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2257', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2258', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2259', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2260', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2261', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2262', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2263', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2264', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2265', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2266', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2267', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2268', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2269', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2270', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2271', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2272', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2273', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2274', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2275', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2276', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2277', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2278', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2279', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2280', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2281', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2282', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2283', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2284', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2285', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2286', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2287', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2288', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2289', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2290', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2291', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2292', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2293', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2294', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2295', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2296', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2297', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2298', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2299', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2300', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2301', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2302', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2303', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2304', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2305', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2306', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2307', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2308', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2309', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2310', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2311', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2312', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2313', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2314', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2315', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2316', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2317', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2318', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2319', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2320', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2321', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2322', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2323', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2324', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2325', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2326', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2327', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2328', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2329', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2330', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2331', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2332', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2333', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2334', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2335', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2336', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2337', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2338', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2339', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2340', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2341', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2342', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2343', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2344', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2345', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2346', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2347', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2348', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2349', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2350', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2351', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2352', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2353', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2354', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2355', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2356', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2357', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2358', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2359', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2360', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2361', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2362', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2363', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2364', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2365', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2366', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2367', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2368', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2369', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2370', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2371', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2372', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2373', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2374', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2375', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2376', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2377', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2378', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2379', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2380', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2381', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2382', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2383', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2384', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2385', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2386', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2387', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2388', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2389', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2390', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2391', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2392', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2393', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2394', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2395', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2396', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2397', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2398', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2399', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2400', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2401', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2402', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2403', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2404', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2405', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2406', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2407', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2408', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2409', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2410', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2411', '7', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2412', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2413', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2414', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2415', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2416', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2417', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2418', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2419', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2420', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2421', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2422', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2423', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2424', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2425', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2426', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2427', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2428', '3', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2429', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2430', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2431', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2432', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2433', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2434', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2435', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2436', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2437', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2438', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2439', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2440', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2441', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2442', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2443', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2444', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2445', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2446', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2447', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2448', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2449', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2450', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2451', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2452', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2453', '0', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2454', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2455', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2456', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2457', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2458', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2459', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2460', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2461', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2462', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2463', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2464', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2465', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2466', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2467', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2468', '2', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2469', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2470', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2471', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2472', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2473', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2474', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2475', '5', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2476', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2477', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2478', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2479', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2480', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2481', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2482', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2483', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2484', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2485', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2486', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2487', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2488', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2489', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2490', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2491', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2492', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2493', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2494', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2495', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2496', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2497', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2498', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2499', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2500', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2501', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2502', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2503', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2504', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2505', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2506', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2507', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2508', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2509', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2510', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2511', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2512', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2513', '5', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2514', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2515', '3', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2516', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2517', '7', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2518', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2519', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2520', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2521', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2522', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2523', '1', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2524', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2525', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2526', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2527', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2528', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2529', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2530', '5', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2531', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2532', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2533', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2534', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2535', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2536', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2537', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2538', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2539', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2540', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2541', '6', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2542', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2543', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2544', '4', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2545', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2546', '1', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2547', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2548', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2549', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2550', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2551', '6', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2552', '1', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2553', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2554', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2555', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2556', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2557', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2558', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2559', '2', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2560', '2', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2561', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2562', '1', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2563', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2564', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2565', '2', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2566', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2567', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2568', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2569', '7', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2570', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2571', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2572', '3', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2573', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2574', '5', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2575', '2', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2576', '1', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2577', '3', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2578', '7', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2579', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2580', '6', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2581', '7', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2582', '2', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2583', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2584', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2585', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2586', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2587', '5', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2588', '7', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2589', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2590', '6', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2591', '6', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2592', '3', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2593', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2594', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2595', '4', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2596', '1', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2597', '4', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2598', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2599', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2600', '4', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2601', '7', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2602', '0', '4', '0.00');
INSERT INTO `mdt_history` VALUES ('2603', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2604', '0', '2', '0.00');
INSERT INTO `mdt_history` VALUES ('2605', '6', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2606', '1', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2607', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2608', '5', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2609', '4', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2610', '6', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2611', '5', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2612', '0', '3', '0.00');
INSERT INTO `mdt_history` VALUES ('2613', '4', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2614', '0', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2615', '0', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2616', '4', '6', '0.00');
INSERT INTO `mdt_history` VALUES ('2617', '3', '7', '0.00');
INSERT INTO `mdt_history` VALUES ('2618', '0', '1', '0.00');
INSERT INTO `mdt_history` VALUES ('2619', '2', '0', '0.00');
INSERT INTO `mdt_history` VALUES ('2620', '3', '5', '0.00');
INSERT INTO `mdt_history` VALUES ('2621', '4', '2', '0.00');

-- ----------------------------
-- Table structure for `mods_wedding`
-- ----------------------------
DROP TABLE IF EXISTS `mods_wedding`;
CREATE TABLE `mods_wedding` (
  `id` int(10) unsigned NOT NULL DEFAULT 0,
  `requesterId` int(10) unsigned NOT NULL DEFAULT 0,
  `partnerId` int(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of mods_wedding
-- ----------------------------

-- ----------------------------
-- Table structure for `olympiad_data`
-- ----------------------------
DROP TABLE IF EXISTS `olympiad_data`;
CREATE TABLE `olympiad_data` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `current_cycle` mediumint(8) unsigned NOT NULL DEFAULT 1,
  `period` varchar(12) NOT NULL DEFAULT 'COMPETITION',
  `olympiad_end` bigint(13) unsigned NOT NULL DEFAULT 0,
  `validation_end` bigint(13) unsigned NOT NULL DEFAULT 0,
  `next_weekly_change` bigint(13) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of olympiad_data
-- ----------------------------
INSERT INTO `olympiad_data` VALUES ('0', '5', 'COMPETITION', '1779548400266', '1760454000731', '1779328286266');

-- ----------------------------
-- Table structure for `olympiad_fights`
-- ----------------------------
DROP TABLE IF EXISTS `olympiad_fights`;
CREATE TABLE `olympiad_fights` (
  `charOneId` int(10) unsigned NOT NULL,
  `charTwoId` int(10) unsigned NOT NULL,
  `charOneClass` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `charTwoClass` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `winner` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `start` bigint(13) unsigned NOT NULL DEFAULT 0,
  `time` bigint(13) unsigned NOT NULL DEFAULT 0,
  `classed` tinyint(1) unsigned NOT NULL DEFAULT 0,
  KEY `charOneId` (`charOneId`),
  KEY `charTwoId` (`charTwoId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of olympiad_fights
-- ----------------------------
INSERT INTO `olympiad_fights` VALUES ('268494406', '268497182', '94', '92', '2', '1770939274224', '83085', '0');
INSERT INTO `olympiad_fights` VALUES ('268497182', '268494406', '92', '94', '2', '1770939630610', '32712', '0');
INSERT INTO `olympiad_fights` VALUES ('268494406', '268497182', '94', '92', '2', '1770940362724', '53873', '0');
INSERT INTO `olympiad_fights` VALUES ('268494406', '268497182', '94', '92', '2', '1770941437860', '35068', '0');
INSERT INTO `olympiad_fights` VALUES ('268497182', '268494406', '92', '94', '2', '1770941702189', '34035', '0');
INSERT INTO `olympiad_fights` VALUES ('268497182', '268494406', '92', '94', '1', '1770946273959', '42703', '0');

-- ----------------------------
-- Table structure for `olympiad_nobles`
-- ----------------------------
DROP TABLE IF EXISTS `olympiad_nobles`;
CREATE TABLE `olympiad_nobles` (
  `char_id` int(10) unsigned NOT NULL DEFAULT 0,
  `class_id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `olympiad_points` int(10) NOT NULL DEFAULT 0,
  `competitions_done` smallint(3) NOT NULL DEFAULT 0,
  `competitions_won` smallint(3) NOT NULL DEFAULT 0,
  `competitions_lost` smallint(3) NOT NULL DEFAULT 0,
  `competitions_drawn` smallint(3) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of olympiad_nobles
-- ----------------------------

-- ----------------------------
-- Table structure for `olympiad_nobles_eom`
-- ----------------------------
DROP TABLE IF EXISTS `olympiad_nobles_eom`;
CREATE TABLE `olympiad_nobles_eom` (
  `char_id` int(10) unsigned NOT NULL DEFAULT 0,
  `class_id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `olympiad_points` int(10) NOT NULL DEFAULT 0,
  `competitions_done` smallint(3) NOT NULL DEFAULT 0,
  `competitions_won` smallint(3) NOT NULL DEFAULT 0,
  `competitions_lost` smallint(3) NOT NULL DEFAULT 0,
  `competitions_drawn` smallint(3) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of olympiad_nobles_eom
-- ----------------------------

-- ----------------------------
-- Table structure for `petition`
-- ----------------------------
DROP TABLE IF EXISTS `petition`;
CREATE TABLE `petition` (
  `oid` int(10) unsigned NOT NULL DEFAULT 0,
  `type` varchar(20) NOT NULL,
  `petitioner_oid` int(10) unsigned NOT NULL DEFAULT 0,
  `submit_date` bigint(20) unsigned NOT NULL DEFAULT 0,
  `content` varchar(256) NOT NULL,
  `is_unread` smallint(1) NOT NULL DEFAULT 1,
  `state` varchar(20) NOT NULL,
  `rate` varchar(10) NOT NULL,
  `feedback` varchar(512) NOT NULL,
  `responders` varchar(150) NOT NULL,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of petition
-- ----------------------------

-- ----------------------------
-- Table structure for `petition_message`
-- ----------------------------
DROP TABLE IF EXISTS `petition_message`;
CREATE TABLE `petition_message` (
  `id` int(10) unsigned NOT NULL DEFAULT 0,
  `petition_oid` int(10) unsigned NOT NULL DEFAULT 0,
  `player_oid` int(10) unsigned NOT NULL DEFAULT 0,
  `type` varchar(20) NOT NULL,
  `player_name` varchar(20) NOT NULL,
  `content` varchar(120) NOT NULL,
  PRIMARY KEY (`id`,`petition_oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of petition_message
-- ----------------------------

-- ----------------------------
-- Table structure for `pets`
-- ----------------------------
DROP TABLE IF EXISTS `pets`;
CREATE TABLE `pets` (
  `item_obj_id` decimal(11,0) NOT NULL DEFAULT 0,
  `name` varchar(16) DEFAULT NULL,
  `level` decimal(11,0) DEFAULT NULL,
  `curHp` decimal(18,0) DEFAULT NULL,
  `curMp` decimal(18,0) DEFAULT NULL,
  `exp` decimal(20,0) DEFAULT NULL,
  `sp` decimal(11,0) DEFAULT NULL,
  `fed` decimal(11,0) DEFAULT NULL,
  PRIMARY KEY (`item_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of pets
-- ----------------------------
INSERT INTO `pets` VALUES ('268493930', null, '15', '195', '95', '127158', '0', '1160');

-- ----------------------------
-- Table structure for `player_droplist_config`
-- ----------------------------
DROP TABLE IF EXISTS `player_droplist_config`;
CREATE TABLE `player_droplist_config` (
  `player_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  PRIMARY KEY (`player_id`,`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of player_droplist_config
-- ----------------------------

-- ----------------------------
-- Table structure for `player_emails`
-- ----------------------------
DROP TABLE IF EXISTS `player_emails`;
CREATE TABLE `player_emails` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL,
  `email_id` int(11) NOT NULL,
  `item_object_id` int(11) NOT NULL,
  `item_id` smallint(6) NOT NULL,
  `count` int(11) NOT NULL,
  `enchant_level` smallint(6) NOT NULL,
  `is_augmented` tinyint(1) NOT NULL DEFAULT 0,
  `augment_id` int(11) DEFAULT NULL,
  `is_paid` tinyint(1) NOT NULL DEFAULT 0,
  `payment_item_id` smallint(6) DEFAULT NULL,
  `payment_item_count` int(11) DEFAULT NULL,
  `status` enum('PENDING','ACCEPTED','REJECTED','CLAIMED','EXPIRED') DEFAULT 'PENDING',
  `expiration_time` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_email_id` (`email_id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of player_emails
-- ----------------------------
INSERT INTO `player_emails` VALUES ('3', '268497182', '268497394', '268493432', '268493493', '1868', '8', '0', '0', '0', '0', null, null, 'EXPIRED', '1758001068312', '1758000768313');
INSERT INTO `player_emails` VALUES ('4', '268497182', '268497394', '268493623', '268493519', '1873', '6', '0', '0', '0', '0', null, null, 'EXPIRED', '1758001576338', '1758001276339');
INSERT INTO `player_emails` VALUES ('5', '268497182', '268497394', '268493368', '268493493', '1868', '16', '0', '0', '0', '0', null, null, 'EXPIRED', '1758002196519', '1758001896520');
INSERT INTO `player_emails` VALUES ('6', '268497182', '268497394', '268493441', '268493491', '6362', '93', '0', '0', '0', '0', null, null, 'CLAIMED', '1758005868696', '1758004068697');
INSERT INTO `player_emails` VALUES ('7', '268497182', '268497394', '268493442', '268493483', '6393', '40', '0', '0', '0', '0', null, null, 'CLAIMED', '1758011311193', '1758004111193');
INSERT INTO `player_emails` VALUES ('8', '268497182', '268497394', '268493443', '268493490', '6360', '49', '0', '0', '0', '1', '57', '57', 'CLAIMED', '1758005972700', '1758004172702');
INSERT INTO `player_emails` VALUES ('9', '268497182', '268497394', '268493674', '268493879', '4037', '5', '0', '0', '0', '1', '57', '200', 'EXPIRED', '1758006116940', '1758004316940');
INSERT INTO `player_emails` VALUES ('10', '268497182', '268497394', '268493543', '268493879', '4037', '5', '0', '0', '0', '0', null, null, 'CLAIMED', '1758471906528', '1758471606530');
INSERT INTO `player_emails` VALUES ('11', '268497182', '268497394', '268493888', '268493872', '1867', '7', '0', '0', '0', '0', null, null, 'CLAIMED', '1758473649032', '1758471849032');
INSERT INTO `player_emails` VALUES ('12', '268497182', '268493309', '268496851', '268496707', '5575', '5000000', '0', '0', '0', '0', null, null, 'CLAIMED', '1769644857745', '1769637657747');
INSERT INTO `player_emails` VALUES ('13', '268497182', '268493309', '268496851', '268493827', '728', '800', '0', '0', '0', '0', null, null, 'CLAIMED', '1769644857745', '1769637657773');
INSERT INTO `player_emails` VALUES ('14', '268497182', '268493309', '268496851', '268494134', '3958', '5', '0', '0', '0', '0', null, null, 'CLAIMED', '1769644857745', '1769637657791');
INSERT INTO `player_emails` VALUES ('15', '268497182', '268493309', '268496851', '268493721', '15017', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1769644857745', '1769637657797');
INSERT INTO `player_emails` VALUES ('16', '268497182', '268493309', '268496851', '268494684', '15022', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1769644857745', '1769637657805');
INSERT INTO `player_emails` VALUES ('17', '268494751', '268494356', '268497321', '268495042', '4037', '23', '0', '0', '0', '0', null, null, 'CLAIMED', '1771263741474', '1771256541475');
INSERT INTO `player_emails` VALUES ('18', '268497182', '268631868', '268493281', '268493879', '4037', '500', '0', '0', '0', '0', null, null, 'CLAIMED', '1771907407958', '1771900207959');
INSERT INTO `player_emails` VALUES ('19', '268494751', '268493578', '268494165', '268495042', '4037', '94', '0', '0', '0', '0', null, null, 'EXPIRED', '1773115805113', '1773108605115');
INSERT INTO `player_emails` VALUES ('20', '268494751', '268493578', '268494165', '268495858', '1878', '55', '0', '0', '0', '0', null, null, 'EXPIRED', '1773115805113', '1773108605128');
INSERT INTO `player_emails` VALUES ('21', '268494751', '268493578', '268494165', '268494309', '1867', '682', '0', '0', '0', '0', null, null, 'EXPIRED', '1773115805113', '1773108605133');
INSERT INTO `player_emails` VALUES ('22', '268494751', '268493578', '268494165', '268493862', '5549', '46', '0', '0', '0', '0', null, null, 'EXPIRED', '1773115805113', '1773108605141');
INSERT INTO `player_emails` VALUES ('23', '268494751', '268493578', '268494165', '268504314', '1868', '1196', '0', '0', '0', '0', null, null, 'EXPIRED', '1773115805113', '1773108605151');
INSERT INTO `player_emails` VALUES ('24', '268494751', '268493578', '268494165', '268495548', '1866', '158', '0', '0', '0', '0', null, null, 'EXPIRED', '1773115805113', '1773108605166');
INSERT INTO `player_emails` VALUES ('25', '268497182', '268494667', '268495223', '268494581', '6393', '35', '0', '0', '0', '0', null, null, 'CLAIMED', '1777092430804', '1777085230805');
INSERT INTO `player_emails` VALUES ('26', '268497182', '268494667', '268495223', '268493879', '4037', '80', '0', '0', '0', '0', null, null, 'CLAIMED', '1777092430804', '1777085230812');
INSERT INTO `player_emails` VALUES ('27', '268497182', '268494667', '268495223', '268493694', '5556', '50', '0', '0', '0', '0', null, null, 'CLAIMED', '1777092430804', '1777085230821');
INSERT INTO `player_emails` VALUES ('28', '268497182', '268494667', '268503911', '268503841', '6578', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202493');
INSERT INTO `player_emails` VALUES ('29', '268497182', '268494667', '268503911', '268503793', '6577', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202499');
INSERT INTO `player_emails` VALUES ('30', '268497182', '268494667', '268503911', '268503792', '6577', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202505');
INSERT INTO `player_emails` VALUES ('31', '268497182', '268494667', '268503911', '268503843', '6578', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202517');
INSERT INTO `player_emails` VALUES ('32', '268497182', '268494667', '268503911', '268503795', '6577', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202524');
INSERT INTO `player_emails` VALUES ('33', '268497182', '268494667', '268503911', '268503794', '6577', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202531');
INSERT INTO `player_emails` VALUES ('34', '268497182', '268494667', '268503911', '268503842', '6578', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202536');
INSERT INTO `player_emails` VALUES ('35', '268497182', '268494667', '268503911', '268503796', '6577', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202541');
INSERT INTO `player_emails` VALUES ('36', '268497182', '268494667', '268503911', '268503837', '6578', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202546');
INSERT INTO `player_emails` VALUES ('37', '268497182', '268494667', '268503911', '268503836', '6578', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202550');
INSERT INTO `player_emails` VALUES ('38', '268497182', '268494667', '268503911', '268503838', '6578', '1', '0', '0', '0', '0', null, null, 'CLAIMED', '1777427402492', '1777420202557');

-- ----------------------------
-- Table structure for `rainbowsprings_attacker_list`
-- ----------------------------
DROP TABLE IF EXISTS `rainbowsprings_attacker_list`;
CREATE TABLE `rainbowsprings_attacker_list` (
  `clanId` int(11) NOT NULL DEFAULT 0,
  `war_decrees_count` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`clanId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of rainbowsprings_attacker_list
-- ----------------------------

-- ----------------------------
-- Table structure for `server_memo`
-- ----------------------------
DROP TABLE IF EXISTS `server_memo`;
CREATE TABLE `server_memo` (
  `var` varchar(20) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`var`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of server_memo
-- ----------------------------
INSERT INTO `server_memo` VALUES ('fishChampionshipEnd', '1779228000517');
INSERT INTO `server_memo` VALUES ('server_crash', 'false');

-- ----------------------------
-- Table structure for `seven_signs`
-- ----------------------------
DROP TABLE IF EXISTS `seven_signs`;
CREATE TABLE `seven_signs` (
  `char_obj_id` int(11) NOT NULL DEFAULT 0,
  `cabal` varchar(8) NOT NULL DEFAULT 'NORMAL',
  `seal` varchar(8) NOT NULL DEFAULT 'NONE',
  `red_stones` int(11) NOT NULL DEFAULT 0,
  `green_stones` int(11) NOT NULL DEFAULT 0,
  `blue_stones` int(11) NOT NULL DEFAULT 0,
  `ancient_adena_amount` decimal(20,0) NOT NULL DEFAULT 0,
  `contribution_score` decimal(20,0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`char_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of seven_signs
-- ----------------------------
INSERT INTO `seven_signs` VALUES ('268493605', 'NORMAL', 'NONE', '0', '0', '0', '0', '0');
INSERT INTO `seven_signs` VALUES ('268494667', 'NORMAL', 'NONE', '0', '0', '0', '0', '0');
INSERT INTO `seven_signs` VALUES ('268494751', 'NORMAL', 'NONE', '0', '0', '0', '0', '0');
INSERT INTO `seven_signs` VALUES ('268497394', 'NORMAL', 'NONE', '0', '0', '0', '0', '0');
INSERT INTO `seven_signs` VALUES ('268498074', 'NORMAL', 'NONE', '0', '0', '0', '0', '0');
INSERT INTO `seven_signs` VALUES ('268509776', 'NORMAL', 'NONE', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for `seven_signs_festival`
-- ----------------------------
DROP TABLE IF EXISTS `seven_signs_festival`;
CREATE TABLE `seven_signs_festival` (
  `festivalId` int(1) NOT NULL DEFAULT 0,
  `cabal` varchar(4) NOT NULL DEFAULT '',
  `cycle` int(4) NOT NULL DEFAULT 0,
  `date` bigint(50) DEFAULT 0,
  `score` int(5) NOT NULL DEFAULT 0,
  `members` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`festivalId`,`cabal`,`cycle`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of seven_signs_festival
-- ----------------------------
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DAWN', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('0', 'DUSK', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DAWN', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('1', 'DUSK', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DAWN', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('2', 'DUSK', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DAWN', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('3', 'DUSK', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DAWN', '19', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '1', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '2', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '3', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '4', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '5', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '6', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '7', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '8', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '9', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '10', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '11', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '12', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '13', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '14', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '15', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '16', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '17', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '18', '0', '0', '');
INSERT INTO `seven_signs_festival` VALUES ('4', 'DUSK', '19', '0', '0', '');

-- ----------------------------
-- Table structure for `seven_signs_status`
-- ----------------------------
DROP TABLE IF EXISTS `seven_signs_status`;
CREATE TABLE `seven_signs_status` (
  `id` int(3) NOT NULL DEFAULT 0,
  `current_cycle` int(10) NOT NULL DEFAULT 1,
  `festival_cycle` int(10) NOT NULL DEFAULT 1,
  `active_period` varchar(16) NOT NULL DEFAULT 'COMPETITION',
  `date` bigint(13) unsigned NOT NULL DEFAULT 0,
  `previous_winner` varchar(8) NOT NULL DEFAULT 'NORMAL',
  `dawn_stone_score` decimal(20,0) NOT NULL DEFAULT 0,
  `dawn_festival_score` int(10) NOT NULL DEFAULT 0,
  `dusk_stone_score` decimal(20,0) NOT NULL DEFAULT 0,
  `dusk_festival_score` int(10) NOT NULL DEFAULT 0,
  `avarice_owner` varchar(8) NOT NULL DEFAULT 'NORMAL',
  `gnosis_owner` varchar(8) NOT NULL DEFAULT 'NORMAL',
  `strife_owner` varchar(8) NOT NULL DEFAULT 'NORMAL',
  `avarice_dawn_score` int(10) NOT NULL DEFAULT 0,
  `gnosis_dawn_score` int(10) NOT NULL DEFAULT 0,
  `strife_dawn_score` int(10) NOT NULL DEFAULT 0,
  `avarice_dusk_score` int(10) NOT NULL DEFAULT 0,
  `gnosis_dusk_score` int(10) NOT NULL DEFAULT 0,
  `strife_dusk_score` int(10) NOT NULL DEFAULT 0,
  `accumulated_bonus0` int(10) NOT NULL DEFAULT 0,
  `accumulated_bonus1` int(10) NOT NULL DEFAULT 0,
  `accumulated_bonus2` int(10) NOT NULL DEFAULT 0,
  `accumulated_bonus3` int(10) NOT NULL DEFAULT 0,
  `accumulated_bonus4` int(10) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of seven_signs_status
-- ----------------------------
INSERT INTO `seven_signs_status` VALUES ('0', '19', '0', 'COMPETITION', '1779166671379', 'NORMAL', '0', '0', '0', '0', 'NORMAL', 'NORMAL', 'NORMAL', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for `siege_clans`
-- ----------------------------
DROP TABLE IF EXISTS `siege_clans`;
CREATE TABLE `siege_clans` (
  `castle_id` tinyint(4) NOT NULL DEFAULT 0,
  `clan_id` int(11) NOT NULL DEFAULT 0,
  `type` varchar(8) DEFAULT 'PENDING',
  PRIMARY KEY (`castle_id`,`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of siege_clans
-- ----------------------------

-- ----------------------------
-- Table structure for `spawn_data`
-- ----------------------------
DROP TABLE IF EXISTS `spawn_data`;
CREATE TABLE `spawn_data` (
  `name` varchar(80) NOT NULL,
  `status` smallint(6) NOT NULL,
  `current_hp` int(10) unsigned NOT NULL,
  `current_mp` int(10) unsigned NOT NULL,
  `loc_x` int(11) NOT NULL DEFAULT 0,
  `loc_y` int(11) NOT NULL DEFAULT 0,
  `loc_z` int(11) NOT NULL DEFAULT 0,
  `heading` mediumint(9) NOT NULL DEFAULT 0,
  `db_value` smallint(6) NOT NULL DEFAULT 0,
  `respawn_time` bigint(20) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of spawn_data
-- ----------------------------
INSERT INTO `spawn_data` VALUES ('amber', '1', '1100996', '4304', '185519', '-105795', '-6304', '50124', '0', '0');
INSERT INTO `spawn_data` VALUES ('anais_brilliance_master', '1', '1144519', '4553', '112800', '-76160', '8', '49152', '0', '0');
INSERT INTO `spawn_data` VALUES ('anakims_nemesis_zakaron', '1', '1777317', '2987', '151394', '88953', '-5424', '8192', '0', '0');
INSERT INTO `spawn_data` VALUES ('ancient_weird_drake', '1', '1052436', '2301', '120466', '111320', '-3048', '16804', '0', '0');
INSERT INTO `spawn_data` VALUES ('antaras_adherent_skyla', '1', '141034', '795', '117660', '103332', '-3584', '30265', '0', '0');
INSERT INTO `spawn_data` VALUES ('antaras_cloe', '1', '935092', '3274', '152260', '110394', '-5520', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('antaras_max', '1', '21551606', '53859', '185708', '114298', '-8216', '32768', '0', '0');
INSERT INTO `spawn_data` VALUES ('antaras_min', '1', '17331988', '43314', '185708', '114298', '-8216', '32768', '0', '0');
INSERT INTO `spawn_data` VALUES ('antaras_normal', '1', '19717847', '49277', '185708', '114298', '-8216', '32768', '0', '0');
INSERT INTO `spawn_data` VALUES ('apepi', '1', '255564', '731', '88558', '176682', '-3448', '24625', '0', '0');
INSERT INTO `spawn_data` VALUES ('archon_susceptor', '1', '288415', '1355', '14963', '118802', '-12088', '33733', '0', '0');
INSERT INTO `spawn_data` VALUES ('atraiban', '1', '235272', '1847', '55546', '206811', '-3736', '43069', '0', '0');
INSERT INTO `spawn_data` VALUES ('baium', '1', '5571187', '7430', '-105200', '-253104', '-15536', '5458', '0', '0');
INSERT INTO `spawn_data` VALUES ('bandit_leader_barda', '1', '451391', '1975', '104251', '-17171', '-1832', '32309', '0', '0');
INSERT INTO `spawn_data` VALUES ('barion', '1', '319791', '1476', '107310', '27148', '-3480', '22401', '0', '0');
INSERT INTO `spawn_data` VALUES ('beastlord_behemoth', '1', '888658', '2987', '123934', '-23440', '-3472', '20124', '0', '0');
INSERT INTO `spawn_data` VALUES ('benom', '1', '1352750', '9037', '11882', '-49216', '-3008', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('bereths_seer_sephia', '1', '451391', '1975', '125328', '102715', '-3288', '21930', '0', '0');
INSERT INTO `spawn_data` VALUES ('betrayer_of_urutu_freki', '1', '103092', '575', '-17599', '-101082', '-2104', '58953', '0', '0');
INSERT INTO `spawn_data` VALUES ('biconne_of_blue_sky', '1', '288415', '1355', '106894', '168586', '-3400', '12756', '0', '0');
INSERT INTO `spawn_data` VALUES ('blinding_fire_barakiel', '1', '888658', '2987', '91506', '-85862', '-2712', '56775', '0', '0');
INSERT INTO `spawn_data` VALUES ('bloody_empress_decarbia', '1', '945900', '3347', '188650', '13402', '-2672', '54816', '0', '0');
INSERT INTO `spawn_data` VALUES ('bloody_priest_rudelto', '1', '875948', '2917', '143955', '110360', '-3928', '32768', '0', '0');
INSERT INTO `spawn_data` VALUES ('bloody_tree_vermilion', '1', '945900', '3347', '146414', '-20620', '-3408', '14640', '0', '0');
INSERT INTO `spawn_data` VALUES ('breka_warlock_pastu', '1', '156584', '860', '90891', '125495', '-2112', '51808', '0', '0');
INSERT INTO `spawn_data` VALUES ('carnage_lord_gato', '1', '369009', '1660', '75468', '-9904', '-2640', '23363', '0', '0');
INSERT INTO `spawn_data` VALUES ('carnamakos', '1', '507285', '1660', '23832', '119374', '-8976', '19675', '0', '0');
INSERT INTO `spawn_data` VALUES ('catseye', '1', '168366', '731', '53857', '102721', '-1056', '43946', '0', '0');
INSERT INTO `spawn_data` VALUES ('cherub_garacsia', '1', '987470', '3643', '113875', '15114', '9560', '21866', '0', '0');
INSERT INTO `spawn_data` VALUES ('chief_mate_tilion', '1', '369009', '1660', '43093', '220089', '-3680', '25093', '0', '0');
INSERT INTO `spawn_data` VALUES ('core', '1', '244871', '1276', '17726', '108915', '-6472', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('corsair_captain_kylon', '1', '148507', '827', '35899', '191786', '-3088', '37827', '0', '0');
INSERT INTO `spawn_data` VALUES ('crazy_mechanic_golem', '1', '702418', '1237', '91608', '16322', '-5128', '24625', '0', '0');
INSERT INTO `spawn_data` VALUES ('cronoss_summons_mumu', '1', '156584', '860', '68606', '203053', '-3560', '8090', '0', '0');
INSERT INTO `spawn_data` VALUES ('cursed_clala', '1', '369009', '1660', '89609', '105760', '-3240', '21750', '0', '0');
INSERT INTO `spawn_data` VALUES ('darkness_shaman_baranka', '1', '418874', '1847', '74471', '-102539', '-960', '41097', '0', '0');
INSERT INTO `spawn_data` VALUES ('degeneration_golem', '1', '1848045', '3202', '168903', '-57767', '-3784', '60496', '0', '0');
INSERT INTO `spawn_data` VALUES ('demon_tempest', '1', '174646', '927', '53460', '143069', '-3856', '8656', '0', '0');
INSERT INTO `spawn_data` VALUES ('demonic_agent_falston', '1', '834231', '2707', '92950', '-75069', '-1800', '59188', '0', '0');
INSERT INTO `spawn_data` VALUES ('discard_guardian', '1', '175392', '426', '47863', '243300', '-6608', '35290', '0', '0');
INSERT INTO `spawn_data` VALUES ('domb_death_cabrio', '1', '888658', '2987', '181110', '11907', '-2720', '32960', '0', '0');
INSERT INTO `spawn_data` VALUES ('doom_blade_tanatos', '1', '1825269', '3130', '130998', '-13238', '-3544', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('dr_chaos_npc', '1', '3862', '1641', '96524', '-111070', '-3328', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('dread_avenger_kraven', '1', '273375', '1296', '62930', '8082', '-3248', '6779', '0', '0');
INSERT INTO `spawn_data` VALUES ('earth_protecter_panathen', '1', '258849', '1237', '125608', '190699', '-3224', '30345', '0', '0');
INSERT INTO `spawn_data` VALUES ('elf_renoa', '1', '121941', '699', '-37670', '198123', '-2696', '51952', '0', '0');
INSERT INTO `spawn_data` VALUES ('enmity_ghost_ramdal', '1', '588136', '2639', '112948', '17611', '-4376', '29460', '0', '0');
INSERT INTO `spawn_data` VALUES ('ereve_deathman', '1', '385670', '1722', '150626', '67743', '-3672', '65130', '0', '0');
INSERT INTO `spawn_data` VALUES ('eva_guardian_millenu', '1', '346037', '2169', '88549', '246142', '-10376', '15158', '0', '0');
INSERT INTO `spawn_data` VALUES ('eyes_of_bereth', '1', '165289', '893', '4752', '189073', '-3656', '54846', '0', '0');
INSERT INTO `spawn_data` VALUES ('faf_herald_lokness', '1', '1777317', '2987', '103456', '157424', '-3608', '24625', '0', '0');
INSERT INTO `spawn_data` VALUES ('fafurions_pagehood_sika', '1', '218810', '1062', '112744', '210045', '-3640', '40405', '0', '0');
INSERT INTO `spawn_data` VALUES ('fairy_queen_timiniel', '1', '539706', '2368', '112733', '46525', '-4640', '16394', '0', '0');
INSERT INTO `spawn_data` VALUES ('fairys_watcher_ruell', '1', '451391', '1975', '125560', '50169', '-3664', '2525', '0', '0');
INSERT INTO `spawn_data` VALUES ('fiercetiger_king_angel', '1', '1637918', '2639', '171241', '85450', '-1912', '24625', '0', '0');
INSERT INTO `spawn_data` VALUES ('flame_stone_golem', '1', '956490', '3420', '144585', '-5587', '-4720', '24444', '0', '0');
INSERT INTO `spawn_data` VALUES ('flamelord_shadar', '1', '165289', '893', '43618', '123878', '-2816', '63481', '0', '0');
INSERT INTO `spawn_data` VALUES ('flamestone_golem', '1', '273375', '1296', '79195', '18299', '-5192', '48824', '0', '0');
INSERT INTO `spawn_data` VALUES ('follower_of_frintessa', '1', '1832600', '22200', '-105200', '-253104', '-15536', '16834', '0', '0');
INSERT INTO `spawn_data` VALUES ('follower_of_frintessa_tr', '1', '2748900', '46620', '-105200', '-253104', '-15536', '16384', '0', '0');
INSERT INTO `spawn_data` VALUES ('frintessa', '1', '791683', '47157', '-105200', '-253104', '-15536', '16384', '0', '0');
INSERT INTO `spawn_data` VALUES ('furious_thieles', '1', '624464', '1975', '114392', '53205', '-3760', '44439', '0', '0');
INSERT INTO `spawn_data` VALUES ('gargoyle_lord_sirocco', '1', '165289', '893', '-16072', '184361', '-3792', '45056', '0', '0');
INSERT INTO `spawn_data` VALUES ('gargoyle_lord_tiphon', '1', '1637918', '2639', '173605', '-24758', '-3640', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('geyser_guardian_hestia', '1', '977229', '3568', '134858', '-116187', '-1192', '57716', '0', '0');
INSERT INTO `spawn_data` VALUES ('ghost_kabed', '1', '451391', '1975', '183637', '24980', '-3168', '9047', '0', '0');
INSERT INTO `spawn_data` VALUES ('ghost_of_peasant_leader', '1', '507285', '1660', '170226', '12014', '-2720', '34060', '0', '0');
INSERT INTO `spawn_data` VALUES ('giant_marpanak', '1', '1461912', '2301', '194538', '54427', '-4368', '16454', '0', '0');
INSERT INTO `spawn_data` VALUES ('giant_wasteland_basil', '1', '127782', '731', '-16391', '175240', '-3472', '15354', '0', '0');
INSERT INTO `spawn_data` VALUES ('golkonda_longhorn', '1', '1974940', '3643', '116956', '15991', '6992', '8538', '0', '0');
INSERT INTO `spawn_data` VALUES ('gordon', '1', '2289038', '4553', '152721', '-42632', '-2840', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('gorgolos', '1', '576851', '2570', '186252', '61174', '-4152', '20010', '0', '0');
INSERT INTO `spawn_data` VALUES ('grandeur_soul_chertuba', '1', '330579', '893', '-91201', '116450', '-3456', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('grave_rabber_khim', '1', '402319', '1784', '175191', '30183', '-3768', '34369', '0', '0');
INSERT INTO `spawn_data` VALUES ('grave_robber_akata', '1', '127782', '731', '48575', '-106191', '-1568', '50856', '0', '0');
INSERT INTO `spawn_data` VALUES ('greyclaw_kutus', '1', '95986', '514', '-54309', '146432', '-2872', '25100', '0', '0');
INSERT INTO `spawn_data` VALUES ('guardian_3_of_garden', '1', '283755', '2301', '77327', '244948', '-10376', '39947', '0', '0');
INSERT INTO `spawn_data` VALUES ('gwindorr', '1', '218810', '1062', '86623', '217067', '-3568', '54238', '0', '0');
INSERT INTO `spawn_data` VALUES ('Hallate_the_death_lord', '1', '924022', '3202', '113012', '17444', '-2120', '27149', '0', '0');
INSERT INTO `spawn_data` VALUES ('handmaiden_of_orfen', '1', '335987', '1537', '41397', '24425', '-4528', '16488', '0', '0');
INSERT INTO `spawn_data` VALUES ('harit_hero_tamash', '1', '624464', '1975', '165624', '88211', '-2368', '50507', '0', '0');
INSERT INTO `spawn_data` VALUES ('harit_tutelar_garangky', '1', '467209', '2039', '166161', '68621', '-3256', '32784', '0', '0');
INSERT INTO `spawn_data` VALUES ('hatos', '1', '526218', '2301', '182663', '53250', '-4352', '41138', '0', '0');
INSERT INTO `spawn_data` VALUES ('heart_of_volcano', '1', '3861', '1657', '189872', '-105152', '-720', '49000', '0', '0');
INSERT INTO `spawn_data` VALUES ('hekaton_prime', '1', '818959', '2639', '191163', '56069', '-7616', '47538', '0', '0');
INSERT INTO `spawn_data` VALUES ('hope_immortality_mardil', '1', '645953', '3058', '113103', '17738', '-1408', '24657', '0', '0');
INSERT INTO `spawn_data` VALUES ('icarus_sample_21', '1', '294846', '1062', '93751', '197598', '-3688', '44674', '0', '0');
INSERT INTO `spawn_data` VALUES ('ikuntai', '1', '134813', '575', '-21804', '152084', '-3048', '33560', '0', '0');
INSERT INTO `spawn_data` VALUES ('ipos_the_death_lord', '1', '1891801', '3347', '154486', '-14771', '-3512', '24576', '0', '0');
INSERT INTO `spawn_data` VALUES ('iron_giant_totem', '1', '392985', '1355', '94006', '19586', '-3496', '65170', '0', '0');
INSERT INTO `spawn_data` VALUES ('istary_papurrion', '1', '288415', '1355', '126763', '174238', '-3016', '15686', '0', '0');
INSERT INTO `spawn_data` VALUES ('jeruna_queen', '1', '208019', '860', '27035', '101512', '-3688', '11484', '0', '0');
INSERT INTO `spawn_data` VALUES ('karte', '1', '352421', '1598', '116734', '27326', '-3304', '48289', '0', '0');
INSERT INTO `spawn_data` VALUES ('katu_van_atui', '1', '352421', '1598', '92837', '8031', '-3904', '61112', '0', '0');
INSERT INTO `spawn_data` VALUES ('kaysha_herald_of_ikaros', '1', '90169', '455', '-47374', '51852', '-5912', '37872', '0', '0');
INSERT INTO `spawn_data` VALUES ('kelbar', '1', '273375', '1296', '107177', '92372', '-2184', '39533', '0', '0');
INSERT INTO `spawn_data` VALUES ('Kernon', '1', '945900', '3347', '113150', '16371', '3960', '1490', '0', '0');
INSERT INTO `spawn_data` VALUES ('ketra_chief_brakki', '1', '1639965', '4553', '145860', '-85318', '-6200', '12325', '0', '0');
INSERT INTO `spawn_data` VALUES ('ketra_commander_tayr', '1', '773553', '4183', '145409', '-81957', '-6008', '59030', '0', '0');
INSERT INTO `spawn_data` VALUES ('ketra_hero_hekaton', '1', '714778', '3718', '148006', '-73555', '-4904', '24345', '0', '0');
INSERT INTO `spawn_data` VALUES ('king_tarlk', '1', '920790', '1537', '80706', '5134', '-3024', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('king_tiger_karuta', '1', '288415', '1355', '75934', '111552', '-2456', '56320', '0', '0');
INSERT INTO `spawn_data` VALUES ('korim', '1', '888658', '2987', '115771', '16302', '1944', '54952', '0', '0');
INSERT INTO `spawn_data` VALUES ('krokian_padisha_sobekk', '1', '935092', '3274', '119760', '157392', '-3736', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('kurikups', '1', '512194', '2235', '42138', '216129', '-3736', '46112', '0', '0');
INSERT INTO `spawn_data` VALUES ('langk_matriarch_rashkos', '1', '198734', '545', '-45049', '219416', '-3680', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('last_lesser_glaki', '1', '977229', '3568', '171694', '55136', '-5920', '51180', '0', '0');
INSERT INTO `spawn_data` VALUES ('last_lesser_olkuth', '1', '1891801', '3347', '189187', '48018', '-5824', '24625', '0', '0');
INSERT INTO `spawn_data` VALUES ('last_lesser_utenus', '1', '598898', '2707', '186972', '56296', '-4576', '49699', '0', '0');
INSERT INTO `spawn_data` VALUES ('leto_chief_talkin', '1', '218810', '1062', '87713', '75847', '-3584', '10020', '0', '0');
INSERT INTO `spawn_data` VALUES ('liliths_oracle_marilion', '1', '507285', '1660', '54588', '180510', '-4976', '12269', '0', '0');
INSERT INTO `spawn_data` VALUES ('lizardman_leader_hellion', '1', '195371', '994', '25873', '123178', '-3720', '57100', '0', '0');
INSERT INTO `spawn_data` VALUES ('lord_ishka', '1', '526218', '2301', '115229', '112119', '-2976', '35371', '0', '0');
INSERT INTO `spawn_data` VALUES ('lost_cat_the_cat_a', '1', '206753', '1028', '88151', '140735', '-3424', '54544', '0', '0');
INSERT INTO `spawn_data` VALUES ('love_reverser_kael', '1', '99367', '545', '-60300', '188279', '-4512', '36951', '0', '0');
INSERT INTO `spawn_data` VALUES ('madness_beast', '1', '87696', '426', '-54057', '84850', '-3544', '35891', '0', '0');
INSERT INTO `spawn_data` VALUES ('magician_kenishee', '1', '100831', '1847', '53313', '205476', '-3728', '45476', '0', '0');
INSERT INTO `spawn_data` VALUES ('malex_herald_of_dagoniel', '1', '90169', '455', '9812', '77040', '-3808', '30401', '0', '0');
INSERT INTO `spawn_data` VALUES ('malruks_oracle_sekina', '1', '848789', '2777', '144625', '-27766', '-1944', '61956', '0', '0');
INSERT INTO `spawn_data` VALUES ('mammons_collector_talos', '1', '103092', '575', '172896', '-214202', '-3520', '52855', '0', '0');
INSERT INTO `spawn_data` VALUES ('manes_lidia', '1', '526218', '2301', '174042', '-11318', '-2880', '14453', '0', '0');
INSERT INTO `spawn_data` VALUES ('master_of_ledflag_shaka', '1', '554640', '1784', '95462', '-22452', '-2096', '36589', '0', '0');
INSERT INTO `spawn_data` VALUES ('meana_agent_of_beres', '1', '127782', '731', '116568', '139395', '-3632', '28080', '0', '0');
INSERT INTO `spawn_data` VALUES ('meanas_anor', '1', '888658', '2987', '156666', '-6009', '-4184', '21344', '0', '0');
INSERT INTO `spawn_data` VALUES ('monster_cyrion', '1', '288415', '1355', '110971', '82483', '-2880', '10683', '0', '0');
INSERT INTO `spawn_data` VALUES ('nakondas', '1', '218810', '1062', '128024', '138469', '-3496', '11593', '0', '0');
INSERT INTO `spawn_data` VALUES ('necrosentinel_guard', '1', '319791', '1476', '82316', '112914', '-3056', '46448', '0', '0');
INSERT INTO `spawn_data` VALUES ('nightmare_drake', '1', '512194', '2235', '154993', '85454', '-3480', '30940', '0', '0');
INSERT INTO `spawn_data` VALUES ('niniel_spirit_eva', '1', '451391', '1975', '82098', '252258', '-10592', '61108', '0', '0');
INSERT INTO `spawn_data` VALUES ('nurka1', '1', '1264000', '1095', '44525', '108867', '-2032', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('nurkas_messenger', '1', '297015', '827', '48504', '120887', '-3928', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('obern_mgr_of_fairyqueen', '1', '507285', '1660', '121937', '63787', '-3520', '51359', '0', '0');
INSERT INTO `spawn_data` VALUES ('oblivion_s_mirror', '1', '352421', '1598', '133610', '86760', '-3592', '15873', '0', '0');
INSERT INTO `spawn_data` VALUES ('ocean_flame_ashakiel', '1', '956490', '3420', '123808', '153408', '-3656', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('orfen', '1', '489744', '3685', '55733', '21906', '-5472', '31311', '0', '0');
INSERT INTO `spawn_data` VALUES ('pagan_warder_cerberon', '1', '1248928', '1975', '-12094', '-240157', '-8160', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('palibati_queen_themis', '1', '888658', '2987', '192446', '22180', '-3608', '12379', '0', '0');
INSERT INTO `spawn_data` VALUES ('pan_draid', '1', '206185', '575', '9226', '169946', '-3824', '45556', '0', '0');
INSERT INTO `spawn_data` VALUES ('papurrion_pingolpin', '1', '402319', '1784', '88398', '257806', '-10376', '11147', '0', '0');
INSERT INTO `spawn_data` VALUES ('partisan_leader_talakin', '1', '233163', '668', '51529', '128225', '-3608', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('patriarch_kuroboros', '1', '214372', '606', '-60584', '190538', '-3688', '24576', '0', '0');
INSERT INTO `spawn_data` VALUES ('premo_prime_the_creature', '1', '390743', '994', '102592', '200765', '-3592', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('priest_hisilrome', '1', '588136', '2639', '168613', '28835', '-3648', '7028', '0', '0');
INSERT INTO `spawn_data` VALUES ('priest_of_kuroboros', '1', '95986', '514', '-62357', '178979', '-3576', '34977', '0', '0');
INSERT INTO `spawn_data` VALUES ('princess_molrang', '1', '103092', '575', '-61003', '126800', '-2912', '21198', '0', '0');
INSERT INTO `spawn_data` VALUES ('queen_ant', '1', '346301', '1480', '-21610', '181594', '-5720', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('queens_nobel_leader', '1', '141034', '795', '29612', '107036', '-3696', '11225', '0', '0');
INSERT INTO `spawn_data` VALUES ('ragraman', '1', '336732', '731', '-52775', '170430', '-3672', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('rahha', '1', '818959', '2639', '117027', '-9127', '-3256', '35497', '0', '0');
INSERT INTO `spawn_data` VALUES ('rayito_the_looter', '1', '184670', '960', '127932', '-160588', '-1232', '12946', '0', '0');
INSERT INTO `spawn_data` VALUES ('redeye_leader_trakia', '1', '165289', '893', '40129', '102048', '-1232', '8192', '0', '0');
INSERT INTO `spawn_data` VALUES ('refuge_hoper_leo', '1', '467209', '2039', '86232', '-8267', '-3032', '16120', '0', '0');
INSERT INTO `spawn_data` VALUES ('remmel', '1', '165289', '893', '10994', '127116', '-3600', '6466', '0', '0');
INSERT INTO `spawn_data` VALUES ('repiro_rot_tree', '1', '273375', '1296', '64212', '16535', '-3592', '19240', '0', '0');
INSERT INTO `spawn_data` VALUES ('retreat_spider_cletu', '1', '331522', '1178', '124029', '75802', '-2616', '28623', '0', '0');
INSERT INTO `spawn_data` VALUES ('revenant_of_sir_calibus', '1', '156584', '860', '51032', '153611', '-3520', '25848', '0', '0');
INSERT INTO `spawn_data` VALUES ('road_scavenger_leader', '1', '218810', '1062', '71983', '124839', '-3648', '3473', '0', '0');
INSERT INTO `spawn_data` VALUES ('roar_skylancer', '1', '1338611', '2987', '134072', '59189', '-1328', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('roaring_seer_kastor', '1', '768537', '2435', '104366', '-3220', '-3352', '26115', '0', '0');
INSERT INTO `spawn_data` VALUES ('sailren', '1', '1639965', '4553', '-113091', '-243942', '-15536', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('sebek', '1', '174646', '927', '76008', '193366', '-3520', '38323', '0', '0');
INSERT INTO `spawn_data` VALUES ('sejarr_s_summoner', '1', '330579', '893', '127950', '133565', '-2920', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('serpent_demon_bifrons', '1', '90169', '455', '-13871', '213968', '-3712', '29769', '0', '0');
INSERT INTO `spawn_data` VALUES ('shacram', '1', '576831', '1355', '114539', '84445', '-2360', '24625', '0', '0');
INSERT INTO `spawn_data` VALUES ('shaman_king_selu', '1', '294846', '1062', '73253', '66781', '-3720', '61409', '0', '0');
INSERT INTO `spawn_data` VALUES ('shax_the_death_lord', '1', '1891801', '3347', '179264', '-8395', '-4896', '49082', '0', '0');
INSERT INTO `spawn_data` VALUES ('shuriel_fire_of_wrath', '1', '977229', '3568', '113224', '16543', '6992', '59358', '0', '0');
INSERT INTO `spawn_data` VALUES ('sorcery_isirr', '1', '451391', '1975', '136227', '94344', '-3712', '56316', '0', '0');
INSERT INTO `spawn_data` VALUES ('soul_collector_acheron', '1', '165289', '893', '43171', '152319', '-2840', '9542', '0', '0');
INSERT INTO `spawn_data` VALUES ('soul_scavenger', '1', '103092', '575', '-45928', '110956', '-3800', '58473', '0', '0');
INSERT INTO `spawn_data` VALUES ('soulless_wild_boar', '1', '512194', '2235', '165007', '93639', '-3080', '6882', '0', '0');
INSERT INTO `spawn_data` VALUES ('spike_stakato_qn_shyid', '1', '714778', '3718', '79542', '-54830', '-6136', '49315', '0', '0');
INSERT INTO `spawn_data` VALUES ('spirit_andras_betrayer', '1', '1256671', '2917', '186464', '-27075', '-1928', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('spirits_of_nellis', '1', '206753', '1028', '122700', '-141187', '-1472', '23493', '0', '0');
INSERT INTO `spawn_data` VALUES ('storm_winged_naga', '1', '1891801', '3347', '138483', '-19143', '-3400', '24576', '0', '0');
INSERT INTO `spawn_data` VALUES ('sukar_wererat_chief', '1', '90169', '455', '-3396', '113361', '-3448', '42613', '0', '0');
INSERT INTO `spawn_data` VALUES ('taik_prefect_arak', '1', '526218', '2301', '169899', '42484', '-4808', '18868', '0', '0');
INSERT INTO `spawn_data` VALUES ('tasaba_patriarch_hellena', '1', '165289', '893', '87860', '166011', '-3448', '59860', '0', '0');
INSERT INTO `spawn_data` VALUES ('tiger_hornet', '1', '107186', '606', '28985', '179818', '-3616', '60602', '0', '0');
INSERT INTO `spawn_data` VALUES ('timak_orc_gosmos', '1', '576831', '1355', '68979', '63849', '-3656', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('timak_orc_hunter_a', '1', '371721', '1296', '67398', '67255', '-3688', '47141', '0', '0');
INSERT INTO `spawn_data` VALUES ('timak_seer_ragoth', '1', '482650', '2104', '66512', '46577', '-3912', '29860', '0', '0');
INSERT INTO `spawn_data` VALUES ('tirak', '1', '116581', '668', '-57421', '186195', '-4968', '1854', '0', '0');
INSERT INTO `spawn_data` VALUES ('tracker_sharuk', '1', '95986', '514', '-56370', '186440', '-3216', '35385', '0', '0');
INSERT INTO `spawn_data` VALUES ('triolls_priest_andreas', '1', '275385', '4553', '-16385', '-53268', '-10448', '16384', '0', '0');
INSERT INTO `spawn_data` VALUES ('turek_mercenary_boss', '1', '168366', '731', '-94551', '100203', '-3472', '5276', '0', '0');
INSERT INTO `spawn_data` VALUES ('unicorn_paniel', '1', '435256', '1911', '125247', '43054', '-3592', '4559', '0', '0');
INSERT INTO `spawn_data` VALUES ('uruka', '1', '1608553', '451', '4863', '-11115', '-3648', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('valakas', '1', '25095443', '49277', '-105200', '-253104', '-15536', '34757', '0', '0');
INSERT INTO `spawn_data` VALUES ('vanor_chief_kandra', '1', '912634', '3130', '116189', '-61966', '-3240', '49377', '0', '0');
INSERT INTO `spawn_data` VALUES ('varka_chief_horuth', '1', '1639965', '4553', '105892', '-43549', '-1672', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('varka_commnder_mos', '1', '773553', '4183', '109016', '-36072', '-880', '9669', '0', '0');
INSERT INTO `spawn_data` VALUES ('varka_hero_shadith', '1', '714778', '3718', '114402', '-39185', '-2472', '41879', '0', '0');
INSERT INTO `spawn_data` VALUES ('verfa', '1', '771340', '1722', '127728', '27688', '-3424', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('vuku_witchdr_gharmash', '1', '148507', '827', '17292', '179205', '-3520', '42672', '0', '0');
INSERT INTO `spawn_data` VALUES ('warden_guillotine', '1', '330579', '893', '51221', '147535', '-3560', '8688', '0', '0');
INSERT INTO `spawn_data` VALUES ('water_couatl_ateka', '1', '218810', '1062', '73822', '201461', '-3768', '60648', '0', '0');
INSERT INTO `spawn_data` VALUES ('water_spirit_lian', '1', '218810', '1062', '82985', '182812', '-3648', '41722', '0', '0');
INSERT INTO `spawn_data` VALUES ('wdragon_priest_sheshark', '1', '912634', '3130', '108096', '157408', '-3672', '0', '0', '0');
INSERT INTO `spawn_data` VALUES ('witch_wimere', '1', '451391', '1975', '93008', '114946', '-3200', '33696', '0', '0');
INSERT INTO `spawn_data` VALUES ('wizard_of_storm_teruk', '1', '218810', '1062', '92137', '84833', '-3688', '32529', '0', '0');
INSERT INTO `spawn_data` VALUES ('zaken', '1', '1293211', '4384', '56195', '218167', '-3224', '5107', '0', '0');
INSERT INTO `spawn_data` VALUES ('zakens_butcher_krantz', '1', '1248928', '1975', '44018', '208098', '-3752', '45337', '0', '0');
INSERT INTO `spawn_data` VALUES ('zombie_lord_crowl', '1', '103092', '575', '-12400', '138094', '-3488', '48197', '0', '0');
INSERT INTO `spawn_data` VALUES ('zombie_lord_farakelsus', '1', '87696', '426', '22615', '80676', '-3200', '58869', '0', '0');
