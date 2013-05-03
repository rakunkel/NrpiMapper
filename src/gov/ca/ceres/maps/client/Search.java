package gov.ca.ceres.maps.client;

import java.util.Iterator;

import edu.ucdavis.gwt.gis.client.CompatibilityTester;
import edu.ucdavis.gwt.gis.client.DataManager;
import edu.ucdavis.gwt.gis.client.Debugger;
import edu.ucdavis.gwt.gis.client.GisClient;
import edu.ucdavis.gwt.gis.client.GisClient.GisClientLoadHandler;
import edu.ucdavis.gwt.gis.client.layers.DataLayer;
import edu.ucdavis.gwt.gis.client.layout.LayerMenuCreateHandler;
import edu.ucdavis.gwt.gis.client.layout.LayerMenuItem;
import edu.ucdavis.gwt.gis.client.toolbar.button.AddFeaturesButton;
import edu.ucdavis.gwt.gis.client.toolbar.menu.AddLayerMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.ExportMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.HelpMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.QueryMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.SaveLoadMenu;
import edu.ucdavis.gwt.gis.client.resources.GadgetResources;
import gov.ca.ceres.maps.client.albumSearch.AlbumSearchWidget;
import edu.ucdavis.cstars.client.ESRI;
import edu.ucdavis.cstars.client.Graphic;
import edu.ucdavis.cstars.client.InfoTemplate;
import edu.ucdavis.cstars.client.MapWidget;
import edu.ucdavis.cstars.client.SpatialReference;
import edu.ucdavis.cstars.client.dojo.Color;
import edu.ucdavis.cstars.client.event.ClickHandler;
import edu.ucdavis.cstars.client.event.MouseEvent;
import edu.ucdavis.cstars.client.event.QueryTaskCompleteHandler;
import edu.ucdavis.cstars.client.geometry.Geometry;
import edu.ucdavis.cstars.client.geometry.Point;
import edu.ucdavis.cstars.client.layers.ArcGISDynamicMapServiceLayer;
import edu.ucdavis.cstars.client.layers.GraphicsLayer;
import edu.ucdavis.cstars.client.layers.ImageParameters;
import edu.ucdavis.cstars.client.symbol.SimpleFillSymbol;
import edu.ucdavis.cstars.client.symbol.SimpleLineSymbol;
import edu.ucdavis.cstars.client.symbol.SimpleMarkerSymbol;
import edu.ucdavis.cstars.client.tasks.FeatureSet;
import edu.ucdavis.cstars.client.tasks.Query;
import edu.ucdavis.cstars.client.tasks.QueryTask;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Search implements EntryPoint {

	private GisClient mapClient = null;
    public MapWidget map = null;
	private AlbumSearchWidget search = new AlbumSearchWidget();
	private AddLayerMenu addLayerMenu = null;
	ProjectQuerySettings projectQuerySettings = null;
	
	// Shape Query
    Query query = Query.create();

    QueryTask queryTaskCounties = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/CountyBoundaries/MapServer/0");
    QueryTask queryTaskCities = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/US_Cities_Counties/MapServer/0");
    QueryTask queryTaskHuc8 = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Inland_Waters/Watershed_Boundaries/MapServer/0");
    QueryTask queryTaskHuc10 = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Inland_Waters/Watershed_Boundaries/MapServer/1");
    QueryTask queryTaskHuc12 = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Inland_Waters/Watershed_Boundaries/MapServer/2");
    QueryTask queryTaskAssembly = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/ElectedOfficials/MapServer/0");
    QueryTask queryTaskSenate = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/ElectedOfficials/MapServer/1");
    QueryTask queryTaskCongress = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/ElectedOfficials/MapServer/2");
    QueryTask queryTaskProjects = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/NRPI/NRPI_points/MapServer/0");	
    QueryTask queryTaskRcd = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/CA_RCD_Boundaries/MapServer/2");
    QueryTask queryTaskRcdRegion = QueryTask.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/CA_RCD_Boundaries/MapServer/0");
    QueryTask queryTaskRwqcbRegion = QueryTask.create("http://gispublic.waterboards.ca.gov/ArcGIS/rest/services/Administrative/Regional_Board_Boundaries/MapServer/1");

    ArcGISDynamicMapServiceLayer hucLayer = null;
    ArcGISDynamicMapServiceLayer countyLayer = null;
    ArcGISDynamicMapServiceLayer cityLayer = null;
    ArcGISDynamicMapServiceLayer electLayer = null;
    ArcGISDynamicMapServiceLayer rcdLayer = null;
    ArcGISDynamicMapServiceLayer rwqcbLayer = null;
    
    public String currentLayer = "";

    String countyLayerId;
    String cityLayerId;
    String hucLayerId;
    String huc8LayerId;
    String huc10LayerId;
    String huc12LayerId;
    String electLayerId;
    String assemblyLayerId;
    String senateLayerId;
    String congressLayerId;
    String rcdLayerId;
    String rcdRegionLayerId;
    String rwqcbLayerId;

    String hucIdLine;
    String distIdLine;
    String distFieldName;

    Color drawColor = null;

    Color countyColor = Color.create(150, 127, 96, 0.4);
    Color cityColor = Color.create(52,52,52, 0.4);
    
    Color rcdColor = Color.create(100, 100, 0, 0.4);
    Color rcdRegionColor = Color.create(100,100,0, 0.4);

    Color rwqcbRegionColor = Color.create(0,0,255, 0.4);

    Color huc8Color = Color.create(0,100,0, 0.4);
    Color huc10Color = Color.create(255,170,0, 0.4);
    Color huc12Color = Color.create(0,197,255, 0.4);

    Color assemblyColor = Color.create(230, 0, 0, 0.4);
    Color senateColor = Color.create(132, 0, 168, 0.4);
    Color congressColor = Color.create(38,115, 0, 0.4);
	
	
	/**
	 * Center the map for the first time
	 **/
	public void centerMap() {
		if( DataManager.INSTANCE.getConfig().hasCenterPoint() ){
			Point centerPoint = Point.create(
						(float) DataManager.INSTANCE.getConfig().getCenterx(), 
						(float) DataManager.INSTANCE.getConfig().getCentery(), 
						SpatialReference.create(4269)
			);
			centerPoint = (Point) Geometry.geographicToWebMercator(centerPoint);
			try {
				map.centerAndZoom(centerPoint, DataManager.INSTANCE.getConfig().getZoomLevel());
			} catch (Exception e) {
				Debugger.INSTANCE.catchException(e, "GisClient", "centerMap()");
			}
		}
	}
	
    public void onModuleLoad() {
		
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
			@Override
			public void onUncaughtException(Throwable e) {
				String stackTrace = "";
				for( int i = 0; i < e.getStackTrace().length; i++ ) {
					StackTraceElement ele = e.getStackTrace()[i];
					stackTrace += ele.toString()+"<br />";
				}
				Debugger.INSTANCE.catchException(e, "Search", "Uncaught exception in CalAtlasMaps<br /><b>Trace</b><br>"+stackTrace);
			}
		});
		
		injectMobileMetaTag();
		
		CompatibilityTester.showDetails(false);
		GisClient.setLayerMenuCreateHandler(new LayerMenuCreateHandler(){
			@Override
			public LayerMenuItem onCreate(VerticalPanel menu, DataLayer dl) {
					BrowseMenuItem item = new BrowseMenuItem();
					item.init(dl);
					menu.add(item);
					return item;
			}
		});
		

		mapClient = new GisClient();
		
		String searchText = Window.Location.getParameter("search");
		if( searchText != null ) {
			search.show();
			search.search(searchText);
		}
		
		mapClient.load(new GisClientLoadHandler(){
			@Override
			public void onLoad() {
				onClientReady();
				initRemoveHandler(map.getMapObject());
				//projectQuerySettings.fundingProgram.loadData();
				//initHostMap(map.getMapObject());
			}
		});

		
	}
	
	private native void initRemoveHandler(JavaScriptObject jso) /*-{
		$wnd.map = jso;
		var that = this;
		$wnd.__gwt_search_removeLayer = function(id) {
			that.@gov.ca.ceres.maps.client.Search::removeLayer(Ljava/lang/String;)(id);
		}
		$wnd.__gwt_search_popGraphic = function(id) {
			that.@gov.ca.ceres.maps.client.Search::popGraphic(I)(id);
		}
		$wnd.getResultList = function() {
			return that.@gov.ca.ceres.maps.client.Search::getProjectIdList()();
		}
	}-*/;
	
	private native void initHostMap(JavaScriptObject jso) /*-{
		$wnd.map = jso;
		//alert(window.map.getTitle());
	}-*/;

    private String getProjectIdList() {
        String str = new String();
      
        for (String x : projectQuerySettings.currentProjectLinks) {
           x = x.substring(x.indexOf("=") + 1,x.length());
           str = str + x + ',';
        }
        return str.substring(0, str.trim().length() - 1); 
     }

	public void onClientReady() {

		map = mapClient.getMapWidget();

		projectQuerySettings = new ProjectQuerySettings();
		projectQuerySettings.setMyHost(this);
		ProjectQueryMenu pqm = new ProjectQueryMenu();
		pqm.button.setMyHost(this);
		
		mapClient.getToolbar().addToolbarMenu(pqm);
		
		addLayerMenu = new AddLayerMenu();
		mapClient.getToolbar().addToolbarMenu(addLayerMenu);
		addLayerMenu.addItem(search.getIcon());	
		addLayerMenu.addItem(new AddFeaturesButton());
		mapClient.getToolbar().addToolbarMenu(new SaveLoadMenu());
		mapClient.getToolbar().addToolbarMenu(new ExportMenu());
		mapClient.getToolbar().addToolbarMenu(new HelpMenu());
		
		projectQuerySettings.setMap(map);
		projectQuerySettings.setPopupPosition(80, 50);
		
		mapClient.expandLegends(true);
		
		if( GisClient.isIE7() || GisClient.isIE8() ) {
			mapClient.getRootPanel().getElement().getStyle().setProperty("border", "1px solid #aaaaaa");
		}
	       ImageParameters imageParameters = ImageParameters.create();
	        imageParameters.setFormat("jpeg");  //set the image type to PNG24, note default is PNG8.
	        int[] visibleHucs = new int[3];
	        
	        countyLayer = ArcGISDynamicMapServiceLayer.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/CountyBoundaries/MapServer");
	        mapClient.getMapWidget().addLayer(countyLayer);
	        countyLayer.setOpacity(0.5);
	        countyLayerId = countyLayer.getId();        
		
	        cityLayer = ArcGISDynamicMapServiceLayer.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/US_Cities_Counties/MapServer");
	        mapClient.getMapWidget().addLayer(cityLayer);
	        cityLayerId = cityLayer.getId();        
		
	        hucLayer = ArcGISDynamicMapServiceLayer.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Inland_Waters/Watershed_Boundaries/MapServer");
	        mapClient.getMapWidget().addLayer(hucLayer);
	        hucLayerId = hucLayer.getId();        
		
	        electLayer = ArcGISDynamicMapServiceLayer.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/ElectedOfficials/MapServer");
	        mapClient.getMapWidget().addLayer(electLayer);
	        electLayerId = electLayer.getId();        

	        rcdLayer = ArcGISDynamicMapServiceLayer.create("http://atlas.resources.ca.gov/ArcGIS/rest/services/Boundaries/CA_RCD_Boundaries/MapServer");
	        mapClient.getMapWidget().addLayer(rcdLayer);
	        rcdLayer.setOpacity(0.2);
	        rcdLayerId = rcdLayer.getId();        

	        rwqcbLayer = ArcGISDynamicMapServiceLayer.create("http://gispublic.waterboards.ca.gov/ArcGIS/rest/services/Administrative/Regional_Board_Boundaries/MapServer");
	        mapClient.getMapWidget().addLayer(rwqcbLayer);
	        rwqcbLayer.setOpacity(0.3);
	        rwqcbLayerId = rwqcbLayer.getId();        

	        //identify proxy page to use if the toJson payload to the geometry service is greater than 2000 characters.
	        //If this null or not available the buffer operation will not work.  Otherwise it will do a http post to the proxy.
	        ESRI.setProxyUrl("esri/proxy.php");
	        ESRI.alwaysUseProxy(false);
	        
	        query.setReturnGeometry(true);
	        SpatialReference sr = SpatialReference.create(102100);
	        query.setOutSpatialReference(sr);        

	        // Project Query
	        Query query2 = Query.create();
	        query2.setReturnGeometry(true);
	        query2.setOutSpatialReference(sr);        
	        query2.setOutFields(new String[]{"TITLE","LINK","DESCRIPTIO"});
	        
	        //mapClient.getMapWidget().getInfoWindow().resize(235, 150);
	        
	        //mapClient.getMapWidget().addMouseUpHandler(new MouseUpHandler(){
	        map.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(MouseEvent event) {
					if( event.hasGraphic() ) return;
					
	        		mapClient.getMapWidget().getInfoWindow().hide();
	        		query.setSpatialRelationship(Query.SpatialRelationshipType.SPATIAL_REL_INTERSECTS);
	        		query.setGeometry(event.getMapPoint());
	        		if(currentLayer.equals("counties")) {
	        			query.setOutFields(new String[]{"NAME_PCASE"});
	        			queryTaskCounties.execute(query);
	        		}
	        		else if(currentLayer.equals("cities")) {
	        			query.setOutFields(new String[]{"NAME"});
	        			queryTaskCities.execute(query);
	        		}
	        		else if(currentLayer.equals("rcds")) {
	        			query.setOutFields(new String[]{"RCDNAME"});
	        			queryTaskRcd.execute(query);
	        		}
	        		else if(currentLayer.equals("rcdRegions")) {
	        			query.setOutFields(new String[]{"RCD"});
	        			queryTaskRcdRegion.execute(query);
	        		}
	        		else if(currentLayer.equals("rwqcbRegions")) {
	        			query.setOutFields(new String[]{"RB_NAME"});
	        			queryTaskRwqcbRegion.execute(query);
	        		}
	        		else if(currentLayer.equals("assembly")) {
	        			query.setOutFields(new String[]{"Boundaries.DBO.CA_State_Assembly.District"});
	        			queryTaskAssembly.execute(query);
	        		}
	        		else if(currentLayer.equals("senate")) {
	        			query.setOutFields(new String[]{"Boundaries.DBO.CA_State_Senators.District"});
	        			queryTaskSenate.execute(query);
	        		}
	        		else if(currentLayer.equals("congress")) {
	        			query.setOutFields(new String[]{"CA_Congressonial_Reps.District"});
	        			queryTaskCongress.execute(query);
	        		}
	        		else if(currentLayer.equals("huc8")) {
	        			query.setOutFields(new String[]{"Name","SourceDataDescription","HUC8"});
	        			queryTaskHuc8.execute(query);
	        			//dojo.byId('messages').innerHTML = "<b>Executing Watershed (huc8) Intersection Query...</b>";
	        		}
	        		else if(currentLayer.equals("huc10")) {
	        			query.setOutFields(new String[]{"Name","SourceDataDescription","HUC10"});
	        			queryTaskHuc10.execute(query);
	        			//dojo.byId('messages').innerHTML = "<b>Executing Watershed (huc10) Intersection Query...</b>";
	        		}
	        		else if(currentLayer.equals("huc12")) {
	        			query.setOutFields(new String[]{"Name","SourceDataDescription","HUC12"});
	        			queryTaskHuc12.execute(query);
	        			//dojo.byId('messages').innerHTML = "<b>Executing Watershed (huc12) Intersection Query...</b>";
	        		}
				}}

	        );
	        	
	        queryTaskCounties.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
			        Graphic firstGraphic = featureSet.getFeatures().get(0);
			        SimpleFillSymbol symbol = SimpleFillSymbol.create(SimpleFillSymbol.StyleType.STYLE_SOLID, SimpleLineSymbol.create(SimpleLineSymbol.StyleType.STYLE_SOLID, countyColor, 1), countyColor);

			        GraphicsLayer.Options options = GraphicsLayer.Options.create();
			        String graphicId = firstGraphic.getAttributes().getString("NAME_PCASE");
			        options.setId(graphicId);    
			        GraphicsLayer glayer = GraphicsLayer.create(options);
			        firstGraphic.setSymbol(symbol);
			        String infoTempContent =  "<br/><A href='#' onclick='__gwt_search_removeLayer(\"" + glayer.getId() + "\");'>Remove Feature</A>";
			        InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent);
			        infoTemplate.setTitle("${NAME_PCASE} County");
			        firstGraphic.setInfoTemplate(infoTemplate);
				    map.addLayer(glayer);
			        glayer.add(firstGraphic);
			        projectQuerySettings.selectedGraphiclist.add(firstGraphic);

				}
	        	
	        });
	        
	        queryTaskCities.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
			        Graphic firstGraphic = featureSet.getFeatures().get(0);
			        SimpleFillSymbol symbol = SimpleFillSymbol.create(SimpleFillSymbol.StyleType.STYLE_SOLID, SimpleLineSymbol.create(SimpleLineSymbol.StyleType.STYLE_SOLID, cityColor, 1), cityColor);

			        GraphicsLayer.Options options = GraphicsLayer.Options.create();
			        String graphicId = firstGraphic.getAttributes().getString("NAME");
			        options.setId(graphicId);    
			        GraphicsLayer glayer = GraphicsLayer.create(options);
			        firstGraphic.setSymbol(symbol);
			        String infoTempContent =  "<br/><A href='#' onclick='__gwt_search_removeLayer(\"" + glayer.getId() + "\");'>Remove Feature</A>";
			        InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent);
			        infoTemplate.setTitle("${NAME}");
			        firstGraphic.setInfoTemplate(infoTemplate);
				    map.addLayer(glayer);
			        glayer.add(firstGraphic);
			        projectQuerySettings.selectedGraphiclist.add(firstGraphic);

				}
	        	
	        });

	        queryTaskRwqcbRegion.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
			        Graphic firstGraphic = featureSet.getFeatures().get(0);
			        SimpleFillSymbol symbol = SimpleFillSymbol.create(SimpleFillSymbol.StyleType.STYLE_SOLID, SimpleLineSymbol.create(SimpleLineSymbol.StyleType.STYLE_SOLID, rwqcbRegionColor, 1), rwqcbRegionColor);

			        GraphicsLayer.Options options = GraphicsLayer.Options.create();
			        String graphicId = firstGraphic.getAttributes().getString("RB_NAME");
			        options.setId(graphicId);    
			        GraphicsLayer glayer = GraphicsLayer.create(options);
			        firstGraphic.setSymbol(symbol);
			        String infoTempContent =  "<br/><A href='#' onclick='__gwt_search_removeLayer(\"" + glayer.getId() + "\");'>Remove Feature</A>";
			        InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent);
			        infoTemplate.setTitle("SWQCB Region: ${RB_NAME}");
			        firstGraphic.setInfoTemplate(infoTemplate);
				    map.addLayer(glayer);
			        glayer.add(firstGraphic);
			        projectQuerySettings.selectedGraphiclist.add(firstGraphic);
				}
	        });

	        queryTaskRcd.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
			        Graphic firstGraphic = featureSet.getFeatures().get(0);
			        SimpleFillSymbol symbol = SimpleFillSymbol.create(SimpleFillSymbol.StyleType.STYLE_SOLID, SimpleLineSymbol.create(SimpleLineSymbol.StyleType.STYLE_SOLID, rcdColor, 1), rcdColor);

			        GraphicsLayer.Options options = GraphicsLayer.Options.create();
			        String graphicId = firstGraphic.getAttributes().getString("RCDNAME");
			        options.setId(graphicId);    
			        GraphicsLayer glayer = GraphicsLayer.create(options);
			        firstGraphic.setSymbol(symbol);
			        String infoTempContent =  "<br/><A href='#' onclick='__gwt_search_removeLayer(\"" + glayer.getId() + "\");'>Remove Feature</A>";
			        InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent);
			        infoTemplate.setTitle("RCD District: ${RCDNAME}");
			        firstGraphic.setInfoTemplate(infoTemplate);
				    map.addLayer(glayer);
			        glayer.add(firstGraphic);
			        projectQuerySettings.selectedGraphiclist.add(firstGraphic);
				}
	        });

	        queryTaskRcdRegion.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
			        Graphic firstGraphic = featureSet.getFeatures().get(0);
			        SimpleFillSymbol symbol = SimpleFillSymbol.create(SimpleFillSymbol.StyleType.STYLE_SOLID, SimpleLineSymbol.create(SimpleLineSymbol.StyleType.STYLE_SOLID, rcdRegionColor, 1), rcdRegionColor);

			        GraphicsLayer.Options options = GraphicsLayer.Options.create();
			        String graphicId = firstGraphic.getAttributes().getString("RCD");
			        options.setId(graphicId);    
			        GraphicsLayer glayer = GraphicsLayer.create(options);
			        firstGraphic.setSymbol(symbol);
			        String infoTempContent =  "<br/><A href='#' onclick='__gwt_search_removeLayer(\"" + glayer.getId() + "\");'>Remove Feature</A>";
			        InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent);
			        infoTemplate.setTitle("RCD Region: ${RCD}");
			        firstGraphic.setInfoTemplate(infoTemplate);
				    map.addLayer(glayer);
			        glayer.add(firstGraphic);
			        projectQuerySettings.selectedGraphiclist.add(firstGraphic);
				}
	        });

	        queryTaskHuc8.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
					renderHucs(featureSet);
				}
	        });
	        queryTaskHuc10.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
					renderHucs(featureSet);
				}
	        });
	        queryTaskHuc12.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
					renderHucs(featureSet);
				}
	        });

	        queryTaskAssembly.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
					renderDists(featureSet);
				}
	        });
	        queryTaskSenate.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
					renderDists(featureSet);
				}
	        });
	        queryTaskCongress.addCompleteHandler(new QueryTaskCompleteHandler(){

				@Override
				public void onComplete(FeatureSet featureSet) {
					renderDists(featureSet);
				}
	        });
	        	
	        setOverlay();
	}

    private void renderDists(FeatureSet featureSet) {

    	Graphic firstGraphic = featureSet.getFeatures().get(0);
    	SimpleFillSymbol symbol = SimpleFillSymbol.create(SimpleFillSymbol.StyleType.STYLE_SOLID, SimpleLineSymbol.create(SimpleLineSymbol.StyleType.STYLE_SOLID, drawColor, 1), drawColor);

    	GraphicsLayer.Options options = GraphicsLayer.Options.create();
    	String graphicId = currentLayer + firstGraphic.getAttributes().getInt(distFieldName);
    	options.setId(graphicId);    
    	GraphicsLayer glayer = GraphicsLayer.create(options);
    	firstGraphic.setSymbol(symbol);
    	String infoTempContent =  distIdLine;
    	infoTempContent +=  "<br/></br><A href='#' onclick='__gwt_search_removeLayer(\"" + glayer.getId() + "\");'>Remove Feature</A>";
    	InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent);
    	firstGraphic.setInfoTemplate(infoTemplate);
    	map.addLayer(glayer);
    	glayer.add(firstGraphic);
    	projectQuerySettings.selectedGraphiclist.add(firstGraphic);

    }

    private void renderHucs(FeatureSet featureSet) {

    	Graphic firstGraphic = featureSet.getFeatures().get(0);
    	SimpleFillSymbol symbol = SimpleFillSymbol.create(SimpleFillSymbol.StyleType.STYLE_SOLID, SimpleLineSymbol.create(SimpleLineSymbol.StyleType.STYLE_SOLID, drawColor, 1), drawColor);

    	GraphicsLayer.Options options = GraphicsLayer.Options.create();
    	String graphicId = firstGraphic.getAttributes().getString("Name");
    	options.setId(graphicId);    
    	GraphicsLayer glayer = GraphicsLayer.create(options);
    	firstGraphic.setSymbol(symbol);
    	String infoTempContent =  hucIdLine;
    	infoTempContent +=  "<br/></br><A href='#' onclick='__gwt_search_removeLayer(\"" + glayer.getId() + "\");'>Remove Feature</A>";
    	InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent);
    	infoTemplate.setTitle("${Name}");
    	firstGraphic.setInfoTemplate(infoTemplate);
    	map.addLayer(glayer);
    	glayer.add(firstGraphic);
    	projectQuerySettings.selectedGraphiclist.add(firstGraphic);

    }
	 private void removeLayer(String id) {
		 // remove the graphic from the list used for project queries
	     projectQuerySettings.removeGraphicByTitle(((GraphicsLayer) map.getLayer(id)).getGraphics().get(0).getTitle());
		 map.removeLayer(map.getLayer(id));
		 map.getInfoWindow().hide();
		 
	}
	
	 private void popGraphic(int id) {
		 // popup the info window of a particular project
		 
		 for (int i = 0; i < DataManager.INSTANCE.getMap().getGraphics().getGraphics().length(); i++) {
			 if (i == id) {
				 DataManager.INSTANCE.getMap().getGraphics().getGraphics().get(i).setSymbol(createHighlightMarker()); 
			 }
			 else {
				 DataManager.INSTANCE.getMap().getGraphics().getGraphics().get(i).setSymbol(createStandardMarker());
			 }
		 }
		 
	}

	 public void removeGraphics() {
		 Iterator<Graphic> iter = projectQuerySettings.selectedGraphiclist.iterator();
		 while (iter.hasNext()) {
			     map.removeLayer(iter.next().getLayer());
		    	 iter.remove();
		 }
	 }
	 
	 public void setOverlay() {
		hucLayer.setVisibility(false);   
        countyLayer.setVisibility(false);   
        cityLayer.setVisibility(false);   
        electLayer.setVisibility(false);
        rcdLayer.setVisibility(false);
        rwqcbLayer.setVisibility(false);
        
        if(currentLayer.equals("counties")) { 
        	countyLayer.setVisibility(true);
            drawColor = countyColor;
        }
        else if(currentLayer.equals("cities")) {
            cityLayer.setVisibility(true);   
            drawColor = cityColor;
        }
        else if(currentLayer.equals("rcds")) {
    	    rcdLayer.setVisibleLayers(new int[]{2});
            rcdLayer.setVisibility(true);   
            drawColor = rcdColor;
        }
        else if(currentLayer.equals("rcdRegions")) {
    	    rcdLayer.setVisibleLayers(new int[]{0});
            rcdLayer.setVisibility(true);   
            drawColor = rcdRegionColor;
        }
        else if(currentLayer.equals("rwqcbRegions")) {
    	    rwqcbLayer.setVisibleLayers(new int[]{1});
            rwqcbLayer.setVisibility(true);   
            drawColor = rwqcbRegionColor;
        }
        else if(currentLayer.equals("huc8")) {
   	       hucLayer.setVisibleLayers(new int[]{0});
           hucIdLine =  "<br/>Hydrologic Unit (HUC8): ${HUC8}";
           drawColor = huc8Color;
           hucLayer.setVisibility(true);   
        }
        else if(currentLayer.equals("huc10")) {
    	   hucLayer.setVisibleLayers(new int[]{1});
           hucIdLine =  "<br/>Hydrologic Unit (HUC10): ${HUC10}";
           drawColor = huc10Color;
           hucLayer.setVisibility(true);   
        }
        else if(currentLayer.equals("huc12")) {
    	   hucLayer.setVisibleLayers(new int[]{2});
           hucIdLine =  "<br/>Hydrologic Unit (HUC12): ${HUC12}";
           drawColor = huc12Color;
           hucLayer.setVisibility(true);   
        }
        else if(currentLayer.equals("assembly")) {
    	    electLayer.setVisibleLayers(new int[]{0});
    	    distFieldName = "Boundaries.DBO.CA_State_Assembly.District";
            distIdLine =  "<br/>California Assembly District: ${" + distFieldName + "}";
            drawColor = assemblyColor;
            electLayer.setVisibility(true);   
         }
        else if(currentLayer.equals("senate")) {
    	    electLayer.setVisibleLayers(new int[]{1});
    	    distFieldName = "Boundaries.DBO.CA_State_Senators.District";
            distIdLine =  "<br/>California Senate District: ${" + distFieldName + "}";
            drawColor = senateColor;
            electLayer.setVisibility(true);   
         }
        else if(currentLayer.equals("congress")) {
    	    electLayer.setVisibleLayers(new int[]{2});
    	    distFieldName = "CA_Congressonial_Reps.District";
            distIdLine =  "<br/>California Congressional District: ${" + distFieldName + "}";
            drawColor = congressColor;
            electLayer.setVisibility(true);   
         }

      }

	
	private class BrowseMenuItem extends LayerMenuItem {
		
		@Override
		public Image getIcon() {
			return new Image(GadgetResources.INSTANCE.server());
		}
		@Override
		public String getText() {
			return "Browse ArcGIS Server";
		}
		@Override
		public void onClick(DataLayer dataLayer) {
			addLayerMenu.getServiceAdder().browseServer(dataLayer.getUrl());
		}
	}
	
	
	private native void injectMobileMetaTag() /*-{
		try {
			var head = $wnd.document.getElementsByTagName('head')[0];
			
			var meta = $wnd.document.createElement('meta');
			meta.setAttribute('name', 'viewport');
			meta.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=1.0; user-scalable=0;');
			head.appendChild(meta);
			
			meta = $wnd.document.createElement('meta');
			meta.setAttribute('name', 'viewport');
			meta.setAttribute('content', 'width=device-width');
			head.appendChild(meta);
			
			meta = $wnd.document.createElement('meta');
			meta.setAttribute('name', 'HandheldFriendly');
			meta.setAttribute('content', 'True');
			head.appendChild(meta);
		} catch (e) {}
	}-*/;

	/**
	 * Create the standard marker.
	 * 
	 * @return SimpleMarkerSymbol
	 */
	private SimpleMarkerSymbol createStandardMarker() {
		return SimpleMarkerSymbol.create(
				SimpleMarkerSymbol.StyleType.STYLE_CIRCLE, 
				14, 
				SimpleLineSymbol.create(
						SimpleLineSymbol.StyleType.STYLE_SOLID,
						Color.create(60, 60, 60, 1),
						1),
				Color.create(34, 120, 218, 1)
		);
	}

	/**
	 * Create the highlight marker.
	 * 
	 * @return SimpleMarkerSymbol
	 */
	private SimpleMarkerSymbol createHighlightMarker() {
		return SimpleMarkerSymbol.create(
				SimpleMarkerSymbol.StyleType.STYLE_CIRCLE, 
				14, 
				SimpleLineSymbol.create(
						SimpleLineSymbol.StyleType.STYLE_SOLID,
						Color.create(100, 0, 0, 1),
						1),
				Color.create(100, 0, 0, 1)
		);
	}
}
