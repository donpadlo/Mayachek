<?php
 //определяю последние координаты
 $userid=GetDef("userid");
 if ($userid!=""){
  $Longitude=0;
  $Latitude=0;
  $type=0;
  $sql="select * from coords where userid=:userid and type=:type order by dt desc limit 1";
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
    //определяю список дат
	  $dtarr=array();	  
	  $sql="select DATE_FORMAT(dt , '%Y-%m-%d' ) as dt from coords where userid=:userid group by DATE_FORMAT(dt , '%Y-%m-%d' ) order by dt desc limit 10";
	  try {
	      $pre=$cfg->db->prepare($sql, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));	
	      $res=$pre->execute(array(':userid' => $userid));	
	      $c=0;
	      while ($row = $pre->fetch(PDO::FETCH_LAZY)){
		  if ($c==0){$curdt=$row["dt"];};
		  $c++;
		  $dtarr[]=$row["dt"];
	      };
	  } catch (PDOException $e) {
	      print "ERROR!: " . $e->getMessage() . "<br/>";
	      die();
	  }  
	  $items="";
	  foreach ($dtarr as $dt) {
	   $items=$items."new ymaps.control.ListBoxItem({data: {content: '$dt'},options:{checkbox:true,selectOnClick:true}}),\n";    
	  };
	  $itemscl="";
	  $cnt=0;
	  foreach ($dtarr as $dt) {
	   $itemscl=$itemscl."cityList.get($cnt).events.add('click', function () {			      
				curdate='$dt';
				LoadTrackByDate('$dt');
			      });\n";    
	   $cnt++;	   
	  };
	  
    //
?>
<div class="container-fluid">
    <div class="row">
	<div id="map" style="width: 100%; height: 100%"></div>
	</div>
    </div>
<script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>     
 <script type="text/javascript">
   tpG=<?php echo $type;?>;
   userid=<?php echo $userid;?>;
   curdate="<?php echo $curdt; ?>";
   //обьект с коллекциями обьектов карт
    function init(){     
        myMap = new ymaps.Map("map", {
            center: [<?php echo "$Longitude,$Latitude"?>],
            zoom: 15
        }),
	objectManager = new ymaps.ObjectManager({
            // Чтобы метки начали кластеризоваться, выставляем опцию.
            clusterize: false,
            // ObjectManager принимает те же опции, что и кластеризатор.
            gridSize: 32,
            clusterDisableClickZoom: true
        });  
   // Даты
	    var cityList = new ymaps.control.ListBox({
		    data: {
			content: 'Выберите дату'
		    },
		    options:{
			
		    },
		    items: [
			<?php echo "$items"; ?>
		    ]
		});
	<?php echo "$itemscl"; ?> 
   //  	
     myCollection = new ymaps.GeoObjectCollection();     
     myCollectUsers= new ymaps.GeoObjectCollection();    	
     
     myMap.controls.add(cityList, {float: 'left'});   
     
     
     mySaveButton = new ymaps.control.Button({
         data:    {content: '* Network'},
         options: {selectOnClick: false,size:'large'}                 
        });     
        mySaveButton.events.add('click', function () {                                                        
	    mySaveButton.data.set("content","* Network");
	    mySaveButton2.data.set("content","GPS");	    
	    tpG=0;
            LoadTrackByDate(curdate);      
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
            LoadTrackByDate(curdate);      
        });                                
        myMap.controls.add(mySaveButton2, {float: 'right'});   	
	LoadTrackByDate(curdate);      
     
     };
     ymaps.ready(init);
function LoadTrackByDate(dt){
//     $.get("index.php?route=gettrack",  // сначала получем список
//	    {userid: userid,type:tpG,dt:dt}, 
//	    function(e) {  		
//		  obj_for_load=JSON.parse(e);   // загружаем JSON в массив     
//		  //очищаем холст
//		      myMap.geoObjects.each(function(ob) {
//		      myMap.geoObjects.remove(ob);  
//		  });
//		  myCollection.removeAll();  
//		  myMap.geoObjects.add(objectManager);
//		  //рисуме
//		  DrrawAll();
//		 }
//     );     
     $.get("index.php?route=gettrackclaster",  // сначала получем список
	    {userid: userid,type:tpG,dt:dt}, 
	    function(e) {  		
		  //очищаем холст
		      myMap.geoObjects.each(function(ob) {
		      myMap.geoObjects.remove(ob);  
			});
		    myCollection.removeAll();  
		    objectManager = new ymaps.ObjectManager({
			// Чтобы метки начали кластеризоваться, выставляем опцию.
			clusterize: false,
			// ObjectManager принимает те же опции, что и кластеризатор.
			gridSize: 32,
			clusterDisableClickZoom: true
		    });  		   
		    objectManager.add(e);
		    myMap.geoObjects.add(objectManager);		
		 }
     );     
};
    function DrrawAll(){
		  for (i in obj_for_load) {		      
//			var myPolyline = new ymaps.Polyline(
//		[[obj_for_load[i]["LongitudeEnd"],obj_for_load[i]["LatitudeEnd"]],[obj_for_load[i]["LongitudeStart"],obj_for_load[i]["LatitudeStart"]]]		
//		    , {
//					   hintContent : "hint",
//					   interactiveZIndex:true,
//					   balloonContent: "ballon"
//				   }, {
//					// Задаем опции геообъекта.
//					// Цвет с прозрачностью.
//					strokeColor: "#ff00ff",
//					interactiveZIndex:true,
//					// Ширину линии.
//					strokeWidth: 2,
//					// Максимально допустимое количество вершин в ломаной.
//					editorMaxPoints: 50,  			       
//				   }
//			);		    
//		    myCollection.add(myPolyline); 		    
		    if (i==1){
			preset="islands#circleIcon";
			color="green";
		    } else {
			preset="islands#ico";
			color="blue";
		    };
		    presetcolor="#ffffff";    
		    myGeoObject = new ymaps.GeoObject({
			// Описание геометрии.
			geometry: {
			    type: "Point",
			    coordinates: [obj_for_load[i]["LongitudeEnd"],obj_for_load[i]["LatitudeEnd"]]
			},            
			// Свойства.
			properties: {
			    // Контент метки.
			    iconContent: "",
			    hintContent: obj_for_load[i]["dt"],
			    balloonContent:obj_for_load[i]["dt"]
			}
			}, {
			// Опции.
			// Иконка метки будет растягиваться под размер ее содержимого.
			preset: preset,
			iconColor: color,
			// Метку можно перемещать.
			draggable: false,
		    }); 
		    myCollection.add(myGeoObject); //добавляем в коллекцию    		    
	    };	 
	    myMap.geoObjects.add(myCollection); // добавляем на холст		   
     };
 </script>
 <?php 
 } else {
   echo "Маячек не найден!";  
 };
?>