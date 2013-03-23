package gov.ca.ceres.maps.client;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import gov.ca.ceres.thesaurustools.client.NameFiller;
import gov.ca.ceres.thesaurustools.client.Selection.Kind;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdavis.cstars.client.Error;
import edu.ucdavis.cstars.client.Graphic;
import edu.ucdavis.cstars.client.InfoTemplate;
import edu.ucdavis.cstars.client.MapWidget;
import edu.ucdavis.cstars.client.Graphic.Attributes;
import edu.ucdavis.cstars.client.callback.QueryTaskCallback;
import edu.ucdavis.cstars.client.dojo.Color;
import edu.ucdavis.cstars.client.event.MapZoomEndHandler;
import edu.ucdavis.cstars.client.geometry.Extent;
import edu.ucdavis.cstars.client.geometry.Geometry;
import edu.ucdavis.cstars.client.geometry.Point;
import edu.ucdavis.cstars.client.layers.LayerInfo;
import edu.ucdavis.cstars.client.symbol.SimpleLineSymbol;
import edu.ucdavis.cstars.client.symbol.SimpleMarkerSymbol;
import edu.ucdavis.cstars.client.tasks.FeatureSet;
import edu.ucdavis.cstars.client.tasks.Query;
import edu.ucdavis.cstars.client.tasks.QueryTask;
import edu.ucdavis.gwt.gis.client.DataManager;
import edu.ucdavis.gwt.gis.client.draw.DrawControl;
import edu.ucdavis.gwt.gis.client.extras.CloseButton;
import edu.ucdavis.gwt.gis.client.extras.ToolTip;
import edu.ucdavis.gwt.gis.client.layers.DataLayer;
import edu.ucdavis.gwt.gis.client.layers.DataLayerHandler;
import edu.ucdavis.gwt.gis.client.layers.FeatureCollectionDataLayer;
import edu.ucdavis.gwt.gis.client.layers.MapServerDataLayer;
import edu.ucdavis.gwt.gis.client.layers.DataLayer.DataLayerLoadHandler;
import edu.ucdavis.gwt.gis.client.layers.DataLayer.DataLayerType;
import edu.ucdavis.gwt.gis.client.resources.GadgetResources;


public class ProjectQuerySettings extends PopupPanel {

	public static final String GFORGE_BASE = "http://projects.atlas.ca.gov/";
	public static final String API_BASE = "http://atlas.ca.gov/cgi-bin/api/";

	public static final String LEADFILL_URL = API_BASE + "nameFillJson?src=lead&mode=substr&node=289&query=";
	public static final String FUNDFILL_URL = API_BASE + "nameFillJson?src=fund&mode=substr&node=289&query=";
	//public static final String FUNDFILL_URL = GFORGE_BASE +  "frs/admin/ceic_visvocab.php?vocab=funding%20program";
	public static final String PLACEFILL_URL = API_BASE + "nameFill2?src=nrpi&name=";
	public static final String PLACETHES_URL = API_BASE + "related?ext=";
	public static final HTML PLACE_HELP = new HTML("It is important to tag your entry with location information. <p>Begin typing in the input field. Matching California place names will be suggested. <p>Use the Advanced Map Tool to create custom locations.");

	private static ProjectQuerySettingsUiBinder uiBinder = GWT.create(ProjectQuerySettingsUiBinder.class);
	interface ProjectQuerySettingsUiBinder extends UiBinder<Widget, ProjectQuerySettings> {}
	
//	public static ProjectQuerySettings INSTANCE = new ProjectQuerySettings();
	public FeatureCollectionDataLayer queryLayer = new FeatureCollectionDataLayer("queryLayer");

    public ArrayList<Graphic> selectedGraphiclist = new ArrayList<Graphic>();
    public ArrayList<String> currentProjectLinks = new ArrayList<String>();
	
	private FlexTable resultTable = new FlexTable();

	final PopupPanel suggestPopup = new PopupPanel(true);
//	private QueryTablePopupContent resultPopup = new QueryTablePopupContent();
	private int queriesReturned = 0;
	
	private Search myHost = null;
	
	public void setMyHost(Search h) {
		myHost = h;
	}
	
	private class QueryTaskWrapper {
		//private LayerInfo info = null;
		
		private boolean canceled = false;

		public QueryTaskWrapper() {
		}
		public QueryTaskCallback getCallback() {
			return callback;
		}
		private QueryTaskCallback callback = new QueryTaskCallback() {
			@Override
			public void onComplete(FeatureSet featureSet) {
				if( canceled ) return;
				
				JsArray<Graphic> features = featureSet.getFeatures();
				String infoTempContent =  "<br/><a href=\"${LINK}\" target=\"_blank\">Full Record</a><br/>${DESCRIPTIO}";
				InfoTemplate infoTemplate = InfoTemplate.create("", infoTempContent); 
		        infoTemplate.setTitle("<div style=\"width:350px;\">${TITLE}</div>");
				//clear();
				
				for( int i = 0; i < features.length(); i++ ) {
			
					String link = "";
					String title = "";
					String geom = "";
					Attributes attrs = features.get(i).getAttributes();
					JsArrayString keys = attrs.getKeys();
					for( int j = 0; j < keys.length(); j++ ) {
						if (keys.get(j).equalsIgnoreCase("LINK")) {
							link = attrs.getString(keys.get(j));
						}
						else if (keys.get(j).equalsIgnoreCase("TITLE")) {
							title = attrs.getString(keys.get(j));
						}
						else if (keys.get(j).equalsIgnoreCase("geometry")) {
							geom = attrs.getString(keys.get(j));
						}
					}
					if (!currentProjectLinks.contains(link)) {
						addRow(title, link);
						currentProjectLinks.add(link);
					}
					features.get(i).setSymbol(createStandardMarker());
					features.get(i).setInfoTemplate(infoTemplate);
					DataManager.INSTANCE.getMap().getGraphics().add(features.get(i));
					//queryLayer.add(features.get(i));
					
				}
				queryCount.setHTML(currentProjectLinks.size() +" Projects in the query results");
			}
				@Override
				public void onError(Error error) {
					if( canceled ) return;
					queriesReturned++;
					// fail silently
//					statusPanel.setQueriesReturned(queriesReturned);
//
//					if( queriesReturned == statusPanel.getTotal() ) {
//						if( graphics.size() == 0 ) {
//							// this hides the point animation
//							noResultsPopup.show(currentPoint);
//						} else {
//							pointAnimation.hide();
//						}
//					}
				}
			};
		}
	
	private LinkedList<QueryTaskWrapper> pendingRequests = new LinkedList<QueryTaskWrapper>();

	@UiField FlowPanel layers;
	@UiField CheckBox highlight;
	@UiField HTML status;
	@UiField RadioButton pointQuery;
	@UiField RadioButton bboxQuery;
	@UiField CloseButton close;
	@UiField SimplePanel outerDisable;
	@UiField ListBox regionType;
	@UiField ListBox projectType;
	@UiField ListBox resourceIssue;
	@UiField ListBox habitat;
	@UiField TextBox searchTerm;
	@UiField NameFiller leadAgency;
	@UiField NameFiller fundingProgram;
	@UiField RadioButton orFilters;
	@UiField RadioButton andFilters;
	@UiField Button queryBtn;
	@UiField Button resetBtn;
	@UiField ScrollPanel resultPanel;
	@UiField HTML queryCount;
	
	private Image toolbarIcon = new Image(GadgetResources.INSTANCE.querymapInactive());
	private LayerSelectPanel selectedPanel = null;
	//private SelectActionPopup selectActionPopup = new SelectActionPopup();
	private String DISABLED_TEXT = "Choose location and content filters to query";
	private String statusText = "";
	private HTML disable = new HTML(new Image(GadgetResources.INSTANCE.querymapInactive()).toString()+"&nbsp;&nbsp;Disable Query Tool", true);
	
	 /** Used by MyUiBinder to instantiate  */
	 public @UiFactory NameFiller makeNameFiller() { // method name is insignificant
		 return new NameFiller(false,true,false);
	 }

	 public Geometry getSelectedGeometry() {

		 if (selectedGraphiclist.isEmpty()) {
			 return null;
		 }
		 else {
			 return selectedGraphiclist.get(0).getGeometry();
		 }
		 
	 }
	 public void removeGraphicByTitle(String t) {
		 Iterator<Graphic> iter = selectedGraphiclist.iterator();
		 while (iter.hasNext()) {
		     if (iter.next().getTitle().equals(t)) {
		    	 iter.remove();
		     }
		 }
	 }

	 public void query() {
		 
		 String qurl = "http://atlas.resources.ca.gov/ArcGIS/rest/services/NRPI/NRPI_points/MapServer/0";

		 clear();
		 QueryTask queryTask = QueryTask.create(qurl);
		 Query q = Query.create();
		 q.setOutFields(new String[] {"TITLE", "LINK", "DESCRIPTIO", "PROJECT_TY", "RESOURCE_I", "HABITAT", "LEAD_AGENC"});
		 q.setReturnGeometry(true);
		 q.setReturnGeometry(true);
		 q.setWhere(getWhereClause());

		 QueryTaskWrapper wrapper = new QueryTaskWrapper();

		 q.setGeometry(DataManager.INSTANCE.getMap().getExtent());

		 queryCount.setHTML("...query in progress");

		 if (!selectedGraphiclist.isEmpty()) {
			 for (Graphic g : selectedGraphiclist) {
				 q.setGeometry(g.getGeometry());
				 queryTask.execute(q, wrapper.getCallback());
			 }
		 }
		 else {
			 queryTask.execute(q, wrapper.getCallback());			 
		 }

	 }
	 
	  
	@UiHandler("queryBtn")
		  void handleClick(ClickEvent e) {
		    query();
	 }

	@UiHandler("resetBtn")
	  void handleClick2(ClickEvent e) {
	    reset();
	}

	public Button getQueryBtn() {
		 return queryBtn;
	}
	 
	 protected ProjectQuerySettings() {

		setWidget(uiBinder.createAndBindUi(this));

		queryLayer.addToMap(DataManager.INSTANCE.getMap());
		regionType.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int selectedIndex = regionType.getSelectedIndex();
				myHost.currentLayer = regionType.getValue(selectedIndex);
				myHost.setOverlay();
			}

		});
		
		leadAgency.setWidth("200px");
		leadAgency.setLabel("Lead Agency");
		leadAgency.setURL(LEADFILL_URL);
		
		fundingProgram.setWidth("200px");
		fundingProgram.setLabel("Funding Program");
		fundingProgram.setURL(FUNDFILL_URL);
		
		getElement().getStyle().setZIndex(8000);
		setWidth("500px");
		
		
//		  Window.addResizeHandler(new ResizeHandler(){
//			@Override
//			public void onResize(ResizeEvent event) {
//				if( isAttached() ) center();
//			}
//		});
		
		setModal(false);  
		setAutoHideEnabled(false);
		setGlassEnabled(false);
		setStyleName("GwtGisPopup");
		setGlassStyleName("GwtGisPopup-glasspanel");
		close.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		outerDisable.add(disable);
		disable.setStyleName("disableQueryButton");
		disable.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				disable();
			}
		});
		
		toolbarIcon.getElement().getStyle().setCursor(Cursor.POINTER);
		toolbarIcon.getElement().getStyle().setProperty("margin", "0 8px 0 7px");
		toolbarIcon.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
	//			if( isActive() ) {
	//				selectActionPopup.show(toolbarIcon);
	//			} else {
				setPopupPosition(50,30);
				show();
	//			}
			}
		});
		toolbarIcon.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				ToolTip.INSTANCE.show(statusText, toolbarIcon);
			}
    	});
		toolbarIcon.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				ToolTip.INSTANCE.hide();
			}
    	});
		
		resultTable.setCellSpacing(2);
		resultPanel.setWidth("100%");
		resultPanel.setHeight("200px");
		resultPanel.add(resultTable);
		disable();

	  }
	

	  public String getWhereClause() {
		String where = "";
		String join = " AND ";
		if (orFilters.getValue()) {
			join = " OR ";
		}
		if (!searchTerm.getValue().isEmpty()) {
			where += " (upper(TITLE) LIKE '%" + searchTerm.getValue().toUpperCase() + "%' OR upper(DESCRIPTIO) LIKE '%" + searchTerm.getValue().toUpperCase() + "%')";
		}
		if (!leadAgency.nameBox.getValue().isEmpty()) {
			if (!where.isEmpty()) { where += join;}
			where += " upper(LEAD_AGENC) LIKE '%" + leadAgency.nameBox.getValue().toUpperCase() + "%'";
		}
		if (!fundingProgram.nameBox.getValue().isEmpty()) {
			if (!where.isEmpty()) { where += join;}
			// strip the agency prepend
			String s = new String(fundingProgram.nameBox.getValue());
			s = s.replaceFirst(".+--\\s", "");
			where += " upper(FUNDING_PR) LIKE '%" + s.toUpperCase().trim() + "%'";
		}
		if (!projectType.getValue(projectType.getSelectedIndex()).equals("0")) {
			if (!where.isEmpty()) { where += join;}
			where += " upper(PROJECT_TY) LIKE '%" + projectType.getValue(projectType.getSelectedIndex()).toUpperCase() + "%'";
		}
		if (!resourceIssue.getValue(resourceIssue.getSelectedIndex()).equals("0")) {
			if (!where.isEmpty()) { where += join;}
			where += " upper(RESOURCE_I) LIKE '%" + resourceIssue.getValue(resourceIssue.getSelectedIndex()).toUpperCase() + "%'";
		}
		if (!habitat.getValue(habitat.getSelectedIndex()).equals("0")) {
			if (!where.isEmpty()) { where += join;}
			where += " upper(HABITAT) LIKE '%" + habitat.getValue(habitat.getSelectedIndex()).toUpperCase() + "%'";
		}
		return where;
	}
	
	public void setMap(MapWidget map) {
		map.addZoomEndHandler(new MapZoomEndHandler() {
			@Override
			public void onZoomEnd(Extent extent, float zoomFactor,
					Point anchor, int level) {
				for( int i = 0; i < layers.getWidgetCount(); i++ ) {
					((LayerSelectPanel) layers.getWidget(i)).update();
				}
			}
		});
		
		LinkedList<DataLayer> datalayers = DataManager.INSTANCE.getDataLayers();
		for( DataLayer dl: datalayers ) {
			dl.addLoadHandler(new DataLayerLoadHandler(){
				@Override
				public void onDataLoaded(DataLayer dataLayer) {
					addDataLayer(dataLayer);
				}
			});
		}
		
		DataManager.INSTANCE.addDataLayerHandler(new DataLayerHandler() {
			@Override
			public void onDataLayerAdd(DataLayer dl) {
				addDataLayer(dl);
			}
			@Override
			public void onDataLayerRemove(DataLayer dl) {
				removeDataLayer(dl);
			}
			@Override
			public void onDataLayerUpdate(DataLayer dl) {
				updateDataLayer(dl);
			}
		});
	}
	
	public Image getToolbarIcon() {
		return toolbarIcon;
	}
	
	public boolean isActive() {
		if( selectedPanel != null ) {
			if( selectedPanel.hasVisibleAndSelectedSubLayer() ) return true;
		}
		return false;
	}
	
	public boolean isPointQuery() {
		return pointQuery.getValue();
	}
	
	public boolean isBboxQuery() {
		return bboxQuery.getValue();
	}

	private void setSelected(LayerSelectPanel lsp) {
		if( selectedPanel != null ) selectedPanel.unselect();
		selectedPanel = lsp;
		
		// make sure the draw tool is not enabled
		if( DrawControl.INSTANCE.isEnabled() ) {
			DrawControl.INSTANCE.enable(false);
		}
	}
	
	public boolean highlight() {
		return highlight.getValue();
	}
	
	public MapServerDataLayer getQueryLayer() {
		if( selectedPanel == null ) return null;
		return selectedPanel.getDataLayer();
	}
	
	public LinkedList<LayerInfo> getSelectedLayerInfos() {
		return selectedPanel.getSelectedLayerInfos();
	}
	
	private void addDataLayer(DataLayer dl) {
		if( dl.getType() == DataLayerType.MapServer ) {
			MapServerDataLayer msdl = (MapServerDataLayer) dl;
			if( !hasDataLayer(msdl) && !msdl.hasError() ) {
				layers.add(new LayerSelectPanel(msdl));
			}
		}
	}
	
	private void removeDataLayer(DataLayer dl) {
		for( int i = 0; i < layers.getWidgetCount(); i++ ) {
			LayerSelectPanel panel = (LayerSelectPanel) layers.getWidget(i);
			if( dl == panel.getDataLayer() ) {
				if( panel == selectedPanel ) disable();
				layers.remove(i);
				return;
			}
		}
	}
	
	private void updateDataLayer(DataLayer dl) {
		for( int i = 0; i < layers.getWidgetCount(); i++ ) {
			LayerSelectPanel panel = (LayerSelectPanel) layers.getWidget(i);
			if( dl == panel.getDataLayer() ) {
				panel.update();
				return;
			}
		}
	}
	
	private boolean hasDataLayer(MapServerDataLayer msdl) {
		for( int i = 0; i < layers.getWidgetCount(); i++ ) {
			if( ((LayerSelectPanel) layers.getWidget(i)).getDataLayer() == msdl ) 
				return true;
		}
		return false;
	}
	
	public void setDataLayer(MapServerDataLayer msdl) {
		for( int i = 0; i < layers.getWidgetCount(); i++ ) {
			if( ((LayerSelectPanel) layers.getWidget(i)).getDataLayer() == msdl ) 
				setSelected((LayerSelectPanel) layers.getWidget(i));
		}
	}

	public void disable() {
		toolbarIcon.setResource(GadgetResources.INSTANCE.querymapInactive());
		if( selectedPanel != null ) {
			selectedPanel.unselect();
			selectedPanel = null;
		}
		statusText = DISABLED_TEXT;
		status.setHTML(statusText);
		DataManager.INSTANCE.getMap().setMapCursor(Cursor.DEFAULT.getCssName());
		disable.setVisible(false);
	}
	
	
	public void selectServiceFromUrl(String url) {
		for( int i = 0; i < layers.getWidgetCount(); i++ ) {
			LayerSelectPanel lsp = (LayerSelectPanel) layers.getWidget(i);
			if( lsp.getDataLayer().getUrl().contentEquals(url) ){
				setSelected(lsp);
				lsp.select();
				lsp.updateStatus();
				highlight.setValue(true);
				return;
			}
		}
	};

	private class LayerSelectPanel extends Composite {
		private MapServerDataLayer dl;
		private CheckBox select = new CheckBox();
		
		private SimplePanel sublayersSp = new SimplePanel();
		private VerticalPanel sublayers = new VerticalPanel();
		
		private static final String INFO_DELIMITER = " - ";
		
		public LayerSelectPanel(MapServerDataLayer datalayer) {
			this.dl = datalayer;
			
			select.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					if( select.getValue() ) {
						setSelected(LayerSelectPanel.this);
						select();
						updateStatus();
					} else {
						disable();
					}
				}
			});

			for( int i = 0; i < dl.getLegendInfo().length(); i++ ) {
				LayerInfo info = dl.getLegendInfo().get(i);
				if( info.getLayerType().contentEquals("Feature Layer") ) {
					CheckBox cb = new CheckBox("<span style='color:#666666'>"+info.getId()+INFO_DELIMITER+info.getName()+"</span>", true);
					cb.getElement().getStyle().setMarginLeft(15, Unit.PX);
					cb.setValue(true);
					sublayers.add(cb);
				}
			}
			
			// there are no feature layers
			if( sublayers.getWidgetCount() == 0 ) {
				select.setEnabled(false);
			}
			
			dl.addLoadHandler(new DataLayerLoadHandler(){
				@Override
				public void onDataLoaded(DataLayer dataLayer) {
					update();
				}
			});
			
			FlowPanel panel = new FlowPanel();
			panel.add(select);
			sublayersSp.setStyleName("subLayerQueryPanel");
			panel.add(sublayersSp);
			sublayersSp.add(sublayers);
			initWidget(panel);
			panel.getElement().getStyle().setPadding(5, Unit.PX);
			unselect();
		}
		
		
		public LinkedList<LayerInfo> getSelectedLayerInfos() {
			LinkedList<LayerInfo> list = new LinkedList<LayerInfo>();
			for( int i = 0; i < sublayers.getWidgetCount(); i++ ) {
				CheckBox cb = (CheckBox) sublayers.getWidget(i);
				if( cb.getValue() ) {
					try {
						int id = Integer.valueOf(cb.getText().split(INFO_DELIMITER)[0]);
						LayerInfo info = getInfo(id);
						if( info != null ) list.add(info);
					} catch (Exception e) {}
				}
			}
			return list;
		}
		
		private LayerInfo getInfo(int id) {
			if( dl.getLegendInfo() != null ) {
				for( int i = 0; i < dl.getLegendInfo().length(); i++ ) {
					if( dl.getLegendInfo().get(i) != null ) {
						if( dl.getLegendInfo().get(i).getId() == id ) {
							return dl.getLegendInfo().get(i);
						}
					}
				}
			}
			return null;
		}
		
		private void select() {
			select.setValue(true);
			sublayersSp.setHeight(sublayers.getOffsetHeight()+"px");
		}
		
		public void unselect() {
			sublayersSp.setHeight("0px");
			select.setValue(false);
		}
		
		public MapServerDataLayer getDataLayer() {
			return dl;
		}
		
		private void updateStatus() {
			if( hasVisibleAndSelectedSubLayer() && select.getValue() ) {
				statusText = "<b>Active:</b> Querying layer "+dl.getLabel();
				status.setHTML(statusText);
				toolbarIcon.setResource(GadgetResources.INSTANCE.querymap());
				DataManager.INSTANCE.getMap().setMapCursor(Cursor.HELP.getCssName());
				disable.setVisible(true);
			} else if ( select.getValue() ) {
				statusText = "<b>Inactive:</b> Layer "+dl.getLabel()+" is select but has no visible layers at this scale.";
				status.setHTML(statusText);
				toolbarIcon.setResource(GadgetResources.INSTANCE.querymapInactive());
				DataManager.INSTANCE.getMap().setMapCursor(Cursor.DEFAULT.getCssName());
				disable.setVisible(true);
			}
		}
		
		public void update() {
			if( select.isEnabled() ) {
				select.setHTML(new Image(GadgetResources.INSTANCE.layers()).toString()+" <span style='font-size:16px;color:#333333'>"+dl.getLabel()+"</span>");
			} else {
				select.setHTML("<span style='font-size:16px;color:#888888'>"+dl.getLabel()+" contains no queryable layers.</span>");
			}
			
			double scale = Extent.getScale(DataManager.INSTANCE.getMap());
			for( int i = 0; i < sublayers.getWidgetCount(); i++ ) {
				CheckBox cb = (CheckBox) sublayers.getWidget(i);
				if( cb.getValue() ) {
					try {
						int id = Integer.valueOf(cb.getText().split(INFO_DELIMITER)[0]);
						LayerInfo info = getInfo(id);
						if( info != null ) {
							
							double minScale = info.getMinScale();
							double maxScale = info.getMaxScale();
							if( minScale < 0 ) minScale = 0;
							if( maxScale < 0 ) maxScale = 0;				
							if( ((minScale >= scale) && (scale >= maxScale)) || (minScale == 0 && maxScale == 0) ) {
								cb.setVisible(true);
							} else {
								cb.setVisible(false);
							}
						}
					} catch (Exception e) {}
				}
			}
			
			if( selectedPanel == this ) {
				sublayersSp.setHeight(sublayers.getOffsetHeight()+"px");
			}
			
			updateStatus();
		}
		
		private boolean hasVisibleAndSelectedSubLayer() {
			for( int i = 0; i < sublayers.getWidgetCount(); i++ ) {
				CheckBox cb = (CheckBox) sublayers.getWidget(i);
				if( cb.isVisible() && cb.getValue() ) return true;
			}
			return false;
		}
		
	}
	public void clear() {
		resultTable.removeAllRows();
		currentProjectLinks.clear();
		queryCount.setHTML(currentProjectLinks.size() +" Projects in the query result");

		//remove all graphics on the maps graphics layer
		DataManager.INSTANCE.getMap().getGraphics().clear();
		
		
	}
	public void reset() {
		clear();
		searchTerm.setValue("");
		leadAgency.clear();
		fundingProgram.clear();
		resourceIssue.setSelectedIndex(0);
		projectType.setSelectedIndex(0);
		habitat.setSelectedIndex(0);
		regionType.setSelectedIndex(0);
		myHost.removeGraphics();
		myHost.currentLayer = "";
		myHost.setOverlay();
		myHost.centerMap();
	    
		
		
	}
	public void addRow(String title, String link) {
		int row = resultTable.getRowCount();
		resultTable.insertRow(row);
		resultTable.setHTML(row, 0, "<span style='color:#444444;font-weight:bold'><a href='"+link+"' onmouseover='__gwt_search_popGraphic(" + row + ");' target='_blank'>"+title+"</a></span>");
	}

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
