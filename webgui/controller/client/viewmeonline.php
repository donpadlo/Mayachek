<?php
 //определяю последние координаты
 $userid=GetDef("userid");
 if ($userid!=""){
  $Longitude=0;
  $Latitude=0;
  $type=0;
  $sql="select * from coords where userid=:userid and type=:type order by id desc limit 1";
    try {
	$pre=$cfg->db->prepare($sql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));	
	$res=$pre->execute(array(':userid' => $userid,':type' => $type));	
	while ($row = $pre->fetch(PDO::FETCH_LAZY)){
	    $Longitude=$row["Longitude"];
	    $Latitude=$row["Latitude"];
	};
    } catch (PDOException $e) {
	print "ERROR!: " . $e->getMessage() . "<br/>";
	die();
    }  
?>
<div class="container-fluid">
    <div class="row">
	<div id="map" style="width: 100%; height: 100%"></div>
	</div>
    </div>
<script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>     
 <script type="text/javascript">
   tpG=<?php echo $type;?>;
   //обьект с коллекциями обьектов карт
    function init(){     
        myMap = new ymaps.Map("map", {
            center: [<?php echo "$Longitude,$Latitude"?>],
            zoom: 15
        });
     myCollection = new ymaps.GeoObjectCollection();     
     myCollectUsers= new ymaps.GeoObjectCollection();    	
     mySaveButton = new ymaps.control.Button({
         data:    {content: '* Network'},
         options: {selectOnClick: false,size:'large'}                 
        });     
        mySaveButton.events.add('click', function () {                                                        
	    mySaveButton.data.set("content","* Network");
	    mySaveButton2.data.set("content","GPS");	    
	    tpG=0;
            LoadPoint(<?php echo $userid;?>); 
        });                                
        myMap.controls.add(mySaveButton, {float: 'right'});   
      mySaveButton2 = new ymaps.control.Button({
         data:    {content: 'GPS'},
         options: {selectOnClick: false,size:'large'}                 
        });     
        mySaveButton2.events.add('click', function () {                                                        	    
	    mySaveButton2.data.set("content","* GPS");
	    mySaveButton.data.set("content","Network");
	    tpG=1;
            LoadPoint(<?php echo $userid;?>); 
        });                                
        myMap.controls.add(mySaveButton2, {float: 'right'});   	
	LoadPoint(<?php echo $userid;?>); 
	var timerId = setInterval(function() {
	    LoadPoint(<?php echo $userid;?>); 
	},10000);
    };
    
    ymaps.ready(init);
    
function LoadPoint(tuserid){
     $.get("index.php?route=getlastpoint",  // сначала получем список
	    {userid: tuserid,type:tpG}, 
	    function(e) {  
		  //очищаем холст      
		      myMap.geoObjects.each(function(ob) {
		      myMap.geoObjects.remove(ob);  
		  });    
		  myCollection.removeAll();  
		  obj_for_load=JSON.parse(e);   // загружаем JSON в массив     		  		      
		    SetPoint(obj_for_load.Longitude,obj_for_load.Latitude,obj_for_load.dt); 		  
		    myMap.setCenter([obj_for_load.Longitude,obj_for_load.Latitude]);
		 }
     );      
};     
function SetPoint(La,Lo,dt){
		    console.log(La,Lo,dt);
		    preset="islands#islands#darkGreenIcon";
		    presetcolor="#ffffff";    
		    myGeoObject = new ymaps.GeoObject({
			// Описание геометрии.
			geometry: {
			    type: "Point",
			    coordinates: [La,Lo]
			},            
			// Свойства.
			properties: {
			    // Контент метки.
			    iconContent: "",
			    hintContent: dt,
			    balloonContent:dt
			}
			}, {
			// Опции.
			// Иконка метки будет растягиваться под размер ее содержимого.
			preset: preset,
			// Метку можно перемещать.
			draggable: false,
		    }); 
		    myCollection.add(myGeoObject); //добавляем в коллекцию    
		    myMap.geoObjects.add(myCollection); // добавляем на холст        
};    
</script>
<?php 
 } else {
   echo "Маячек не найден!";  
 };
?>