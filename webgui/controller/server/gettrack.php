<?php
$userid=GetDef("userid");
$type=GetDef("type");
$dt=GetDef("dt");
 if ($userid!=""){
  //$sql="select * from coords where userid=:userid and type=:type order by id desc";
  //select dt,Longitude,Latitude from coords where userid=26 and type=0 and dt between "2017-06-29 00:00:00" and "2017-06-29 23:59:59" group by Longitude,Latitude order by id desc;
  $sql="select * from (select dt,Longitude,Latitude from coords where userid=:userid and type=:type and dt between :dtstart and :dtend group by Longitude order by dt desc) as it group by Latitude order by dt";
    try {
	$pre=$cfg->db->prepare($sql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));	
	$res=$pre->execute(array(':userid' => $userid,':type' => $type,':dtstart'=>$dt." 00:00:00",':dtend'=>$dt." 23:59:59"));	
	$cnt=0;
	$coords=array();
	$oldLo="";$oldLa="";
	while ($row = $pre->fetch(PDO::FETCH_LAZY)){
	    $coords[$cnt]["LongitudeEnd"]=$row["Longitude"];
	    $coords[$cnt]["LatitudeEnd"]=$row["Latitude"];
	    if ($oldLo==""){
		$coords[$cnt]["LongitudeStart"]=$row["Longitude"];
		$coords[$cnt]["LatitudeStart"]=$row["Latitude"];		
	    } else {
		$coords[$cnt]["LongitudeStart"]=$oldLo;
		$coords[$cnt]["LatitudeStart"]=$oldLa;				
	    };
	    $coords[$cnt]["dt"]=$row["dt"];				
	    $oldLo=$row["Longitude"];
	    $oldLa=$row["Latitude"];
	    $cnt++;
	};
	echo json_encode($coords);
    } catch (PDOException $e) {
	print "ERROR!: " . $e->getMessage() . "<br/>";
	die();
    }  
};
?>    