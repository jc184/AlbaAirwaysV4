-- phpMyAdmin SQL Dump
-- version 4.5.0.2
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: May 02, 2017 at 05:12 PM
-- Server version: 10.0.17-MariaDB
-- PHP Version: 5.6.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `albaairwaysv4`
--

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

CREATE TABLE `booking` (
  `BookingId` int(11) NOT NULL,
  `NoOfAdults` int(11) NOT NULL,
  `NoOfChildren` int(11) NOT NULL,
  `NoOfInfants` int(11) NOT NULL,
  `TicketType` varchar(12) NOT NULL,
  `SeatType` varchar(12) NOT NULL,
  `FlightId` int(11) NOT NULL,
  `CustomerId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `booking`
--

--INSERT INTO `booking` (`BookingId`, `NoOfAdults`, `NoOfChildren`, `NoOfInfants`, `TicketType`, `SeatType`, `FlightId`, `CustomerId`) VALUES
--(1, 1, 2, 0, 'SINGLE', 'ECONOMY', 1, 1),
--(2, 2, 2, 0, 'RETURN', 'ECONOMY', 2, 3);

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `CustomerId` int(11) NOT NULL,
  `FirstName` varchar(45) NOT NULL,
  `Surname` varchar(45) NOT NULL,
  `ContactNo` varchar(45) NOT NULL,
  `EmailAddress` varchar(45) NOT NULL,
  `LoginName` varchar(45) NOT NULL,
  `LoginPassword` varchar(45) NOT NULL,
  `DateOfBirth` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customer`
--

--INSERT INTO `customer` (`CustomerId`, `FirstName`, `Surname`, `ContactNo`, `EmailAddress`, `LoginName`, `LoginPassword`, `DateOfBirth`) VALUES
--(1, 'james', 'chalmers', '0123456789', 'james.chalmers184@gmail.com', 'jc184', '1Acheilidh1', '1966-04-03'),
--(2, 'john', 'smith', '0123456789', 'js123@gmail.com', 'js123', 'password', '1992-04-25'),
--(3, 'Jane', 'Doe', '1111122222', 'jane.doe@gmail.com', 'jane.doe', 'password', '1997-07-19');

-- --------------------------------------------------------

--
-- Table structure for table `flight`
--

CREATE TABLE `flight` (
  `FlightId` int(11) NOT NULL,
  `FlightDate` date NOT NULL,
  `PassengerCount` int(11) NOT NULL,
  `LeaveDateTime` datetime NOT NULL,
  `ArrivalDateTime` datetime NOT NULL,
  `RouteId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flight`
--

--INSERT INTO `flight` (`FlightId`, `FlightDate`, `PassengerCount`, `LeaveDateTime`, `ArrivalDateTime`, `RouteId`) VALUES
--(1, '2017-04-24', 0, '2017-04-22 10:00:00', '2017-04-22 10:40:00', 1),
--(2, '2017-04-23', 0, '2017-04-23 09:10:00', '2017-04-23 10:00:00', 2);

-- --------------------------------------------------------

--
-- Table structure for table `passenger`
--

CREATE TABLE `passenger` (
  `PassengerId` int(11) NOT NULL,
  `PassengerName` varchar(45) NOT NULL,
  `PassengerType` varchar(45) NOT NULL,
  `FlightId` int(11) NOT NULL,
  `SeatNo` int(11) NOT NULL,
  `BookingId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `passenger`
--

--INSERT INTO `passenger` (`PassengerId`, `PassengerName`, `PassengerType`, `FlightId`, `SeatNo`, `BookingId`) VALUES
--(1, 'James Chalmers', 'BUSINESS', 1, 6, 1);

-- --------------------------------------------------------

--
-- Table structure for table `route`
--

CREATE TABLE `route` (
  `RouteId` int(11) NOT NULL,
  `RouteName` varchar(45) NOT NULL,
  `AirportFrom` varchar(45) NOT NULL,
  `AirportTo` varchar(45) NOT NULL,
  `AdultFare` decimal(10,0) NOT NULL,
  `ChildFare` decimal(10,0) NOT NULL,
  `InfantFare` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `route`
--

--INSERT INTO `route` (`RouteId`, `RouteName`, `AirportFrom`, `AirportTo`, `AdultFare`, `ChildFare`, `InfantFare`) VALUES
--(1, 'Edinburgh to Wick', '0', '5', '75', '38', '19'),
--(2, 'Edinburgh to Islay', '0', '1', '86', '43', '22'),
--(3, 'Edinburgh to Kirkwall', '0', '3', '78', '39', '19'),
--(4, 'Edinburgh to Sumburgh', '0', '4', '90', '45', '22');

-- --------------------------------------------------------

--
-- Table structure for table `seat`
--

CREATE TABLE `seat` (
  `SeatNo` int(11) NOT NULL,
  `SeatType` varchar(45) NOT NULL,
  `BookingId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `seat`
--

--INSERT INTO `seat` (`SeatNo`, `SeatType`, `BookingId`) VALUES
--(1, 'ECONOMY', NULL),
--(2, 'ECONOMY', NULL),
--(3, 'ECONOMY', NULL),
--(4, 'ECONOMY', NULL),
--(5, 'ECONOMY', NULL),
--(6, 'FIRSTCLASS', NULL),
--(7, 'FIRSTCLASS', NULL),
--(8, 'FIRSTCLASS', NULL),
--(9, 'FIRSTCLASS', NULL),
--(10, 'FIRSTCLASS', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `booking`
--
ALTER TABLE `booking`
  ADD PRIMARY KEY (`BookingId`),
  ADD KEY `fk_Booking_Flight1_idx` (`FlightId`),
  ADD KEY `fk_Booking_Customer1_idx` (`CustomerId`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`CustomerId`);

--
-- Indexes for table `flight`
--
ALTER TABLE `flight`
  ADD PRIMARY KEY (`FlightId`),
  ADD KEY `fk_Flight_Route1_idx` (`RouteId`);

--
-- Indexes for table `passenger`
--
ALTER TABLE `passenger`
  ADD PRIMARY KEY (`PassengerId`),
  ADD KEY `fk_Passenger_Flight1_idx` (`FlightId`),
  ADD KEY `fk_Passenger_Seat1_idx` (`SeatNo`),
  ADD KEY `fk_Passenger_Booking1_idx` (`BookingId`);

--
-- Indexes for table `route`
--
ALTER TABLE `route`
  ADD PRIMARY KEY (`RouteId`);

--
-- Indexes for table `seat`
--
ALTER TABLE `seat`
  ADD PRIMARY KEY (`SeatNo`),
  ADD KEY `fk_Seat_Booking1_idx` (`BookingId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `booking`
--
ALTER TABLE `booking`
  MODIFY `BookingId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `CustomerId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `flight`
--
ALTER TABLE `flight`
  MODIFY `FlightId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `passenger`
--
ALTER TABLE `passenger`
  MODIFY `PassengerId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `route`
--
ALTER TABLE `route`
  MODIFY `RouteId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `booking`
--
ALTER TABLE `booking`
  ADD CONSTRAINT `fk_Booking_Customer1` FOREIGN KEY (`CustomerId`) REFERENCES `customer` (`CustomerId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Booking_Flight1` FOREIGN KEY (`FlightId`) REFERENCES `flight` (`FlightId`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `flight`
--
ALTER TABLE `flight`
  ADD CONSTRAINT `fk_Flight_Route1` FOREIGN KEY (`RouteId`) REFERENCES `route` (`RouteId`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `passenger`
--
ALTER TABLE `passenger`
  ADD CONSTRAINT `fk_Passenger_Booking1` FOREIGN KEY (`BookingId`) REFERENCES `booking` (`BookingId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Passenger_Flight1` FOREIGN KEY (`FlightId`) REFERENCES `flight` (`FlightId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Passenger_Seat1` FOREIGN KEY (`SeatNo`) REFERENCES `seat` (`SeatNo`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `seat`
--
ALTER TABLE `seat`
  ADD CONSTRAINT `fk_Seat_Booking1` FOREIGN KEY (`BookingId`) REFERENCES `booking` (`BookingId`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;


