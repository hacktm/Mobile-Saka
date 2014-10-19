<?php

	require_once 'database/db_connect.php';
	include_once 'gcm/GCM.php';

	$phone = $_REQUEST['phone'];
	$price = $_REQUEST['price'];
	$name = $_REQUEST['name'];
	$description = "";//$_REQUEST['description'];
	$smoker = "";//$_REQUEST['smoker'];
	$p_lat = $_REQUEST['p_lat'];
	$p_lon = $_REQUEST['p_lon'];
	$p_address = "";//$_REQUEST['p_address'];
	$d_lat = $_REQUEST['d_lat'];
	$d_lon = $_REQUEST['d_lon'];
	$d_address = "";//$_REQUEST['d_address'];

	$query = mysql_query("insert into requests (phone, price, name, description, smoker, p_lat, p_lon, p_address, d_lat, d_lon, d_address) values ('$phone', '$price', '$name', '$description', '$smoker', '$p_lat', '$p_lon', '$p_address', '$d_lat', '$d_lon', '$d_address')");

	$output['result']='1';

?>