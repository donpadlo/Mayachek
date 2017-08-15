<?php
$userid=GetDef("userid");
$type=GetDef("type");
$dt=GetDef("dt");
 if ($userid!=""){
  $sql="select * from (select dt,Longitude,Latitude from coords where userid=:userid and type=:type and dt between :dtstart and :dtend group by Longitude order by dt desc) as it group by Latitude order by dt desc";
    try {
	$pre=$cfg->db->prepare($sql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));	
	$res=$pre->execute(array(':userid' => $userid,':type' => $type,':dtstart'=>$dt." 00:00:00",':dtend'=>$dt." 23:59:59"));	
	$cnt=0;
	$coords=array();
	while ($row = $pre->fetch(PDO::FETCH_LAZY)){
	    $coords[$cnt]["Longitude"]=$row["Longitude"];
	    $coords[$cnt]["Latitude"]=$row["Latitude"];
	    $coords[$cnt]["dt"]=$row["dt"];
	    $cnt++;
	};
	//формируем json
	$jsn='{"type": "FeatureCollection",';
	$jsn=$jsn.'"features": [';
	$cnt=0;	
	foreach ($coords as $cr) {
	    $dt=$cr["dt"];
	    $lo=$cr["Longitude"];
	    $la=$cr["Latitude"];
	    if ($cnt==0){
		$preset="islands#circleIcon";
		$color="green";
	    } else {
		$preset="islands#ico";
		$color="blue";
	    };
	    $jsn=$jsn.'{"type": "Feature", "id": '.$cnt.', "geometry": {"type": "Point", "coordinates": ['.$lo.', '.$la.']}, "properties": {"balloonContent": "'.$dt.'","clusterCaption": "'.$dt.'", "hintContent": "'.$dt.'"}, "options": {"iconColor": "'.$color.'", "preset": "'.$preset.'"}}';
	    $cnt++;
	    if (count($coords)>$cnt){$jsn=$jsn.',';};
	}
	$jsn=$jsn.']}';	
	echo $jsn;
    } catch (PDOException $e) {
	print "ERROR!: " . $e->getMessage() . "<br/>";
	die();
    }  
};
?>    