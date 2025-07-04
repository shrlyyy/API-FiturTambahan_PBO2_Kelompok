-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for fiturtambahan-pbo2
CREATE DATABASE IF NOT EXISTS `fiturtambahan-pbo2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `fiturtambahan-pbo2`;

-- Dumping structure for table fiturtambahan-pbo2.cashier
CREATE TABLE IF NOT EXISTS `cashier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(30) DEFAULT NULL,
  `password` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table fiturtambahan-pbo2.cashier: ~2 rows (approximately)
INSERT INTO `cashier` (`id`, `username`, `password`) VALUES
	(1, 'kasir01', 'aitibieses'),
	(2, 'kasir02', 'mantapbetul');

-- Dumping structure for table fiturtambahan-pbo2.customer
CREATE TABLE IF NOT EXISTS `customer` (
  `id` varchar(4) NOT NULL,
  `name` varchar(40) NOT NULL,
  `phoneNumber` bigint NOT NULL,
  `address` varchar(250) NOT NULL,
  `createdBy` varchar(30) DEFAULT NULL,
  `editedBy` varchar(30) DEFAULT NULL,
  `deletedBy` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phoneNumber` (`phoneNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table fiturtambahan-pbo2.customer: ~1 rows (approximately)
INSERT INTO `customer` (`id`, `name`, `phoneNumber`, `address`, `createdBy`, `editedBy`, `deletedBy`) VALUES
	('C001', 'lemon', 81234567, 'purnama', 'kasir01', 'kasir01', NULL);

-- Dumping structure for table fiturtambahan-pbo2.product
CREATE TABLE IF NOT EXISTS `product` (
  `id` int NOT NULL,
  `code` varchar(4) NOT NULL DEFAULT '',
  `name` varchar(50) NOT NULL DEFAULT '',
  `category` varchar(30) NOT NULL DEFAULT '',
  `price` int NOT NULL DEFAULT '0',
  `stock` int NOT NULL,
  `createdBy` varchar(30) DEFAULT NULL,
  `editedBy` varchar(30) DEFAULT NULL,
  `deletedBy` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table fiturtambahan-pbo2.product: ~0 rows (approximately)

-- Dumping structure for table fiturtambahan-pbo2.reservation
CREATE TABLE IF NOT EXISTS `reservation` (
  `reservationId` varchar(4) NOT NULL,
  `customerId` varchar(4) NOT NULL,
  `reservationDate` date NOT NULL,
  `reservationTime` time NOT NULL,
  `reservedTable` varchar(3) NOT NULL,
  `numberOfPeople` int NOT NULL,
  `createdBy` varchar(30) DEFAULT NULL,
  `editedBy` varchar(30) DEFAULT NULL,
  `deletedBy` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`reservationId`),
  KEY `fk_customer` (`customerId`),
  CONSTRAINT `fk_customer` FOREIGN KEY (`customerId`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table fiturtambahan-pbo2.reservation: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
