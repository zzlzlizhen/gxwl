/*
SQLyog Ultimate v9.20 
MySQL - 5.7.23 : Database - jg
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`jg` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `jg`;

/*Table structure for table `cars` */

DROP TABLE IF EXISTS `cars`;

CREATE TABLE `cars` (
  `uuid` bigint(16) NOT NULL AUTO_INCREMENT,
  `customerUuid` bigint(16) DEFAULT NULL,
  `goodsUuid` varchar(40) DEFAULT NULL,
  `buyNum` bigint(16) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `cars` */

/*Table structure for table `goods` */

DROP TABLE IF EXISTS `goods`;

CREATE TABLE `goods` (
  `uuid` bigint(16) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) DEFAULT NULL,
  `imgPath` varchar(40) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `goods` */

/*Table structure for table `main_order` */

DROP TABLE IF EXISTS `main_order`;

CREATE TABLE `main_order` (
  `uuid` bigint(16) NOT NULL AUTO_INCREMENT,
  `customerUuid` bigint(16) DEFAULT NULL,
  `orderTime` timestamp NULL DEFAULT NULL,
  `totalMoney` double DEFAULT NULL,
  `saveMoney` double DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `main_order` */

/*Table structure for table `stock` */

DROP TABLE IF EXISTS `stock`;

CREATE TABLE `stock` (
  `uuid` bigint(16) NOT NULL AUTO_INCREMENT,
  `goodsUuid` bigint(16) DEFAULT NULL,
  `storeNum` bigint(16) DEFAULT NULL COMMENT '库存数量',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `stock` */

/*Table structure for table `sub_order` */

DROP TABLE IF EXISTS `sub_order`;

CREATE TABLE `sub_order` (
  `uudi` bigint(16) NOT NULL AUTO_INCREMENT,
  `orderUuid` bigint(16) DEFAULT NULL,
  `goodsUuid` bigint(16) DEFAULT NULL,
  `orderNum` bigint(16) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `money` double DEFAULT NULL,
  `saveMoney` double DEFAULT NULL,
  PRIMARY KEY (`uudi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sub_order` */

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `uuid` bigint(16) NOT NULL AUTO_INCREMENT COMMENT '客户管理唯一id',
  `customerId` varchar(40) DEFAULT NULL COMMENT '客户编号',
  `pwd` varchar(40) DEFAULT NULL COMMENT '密码',
  `showName` varchar(40) DEFAULT NULL,
  `trueName` varchar(40) DEFAULT NULL,
  `registerTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

/*Data for the table `users` */

insert  into `users`(`uuid`,`customerId`,`pwd`,`showName`,`trueName`,`registerTime`) values (1,'23','1234','12','zsm',NULL),(16,'c2','111111','zsm','lz',NULL),(19,'c3','111111','zsm','lz',NULL),(21,'c4','111111','zsm','lz',NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
