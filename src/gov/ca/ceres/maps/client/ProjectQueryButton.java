package gov.ca.ceres.maps.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Image;

import edu.ucdavis.cstars.client.event.ClickHandler;
import edu.ucdavis.gwt.gis.client.resources.GadgetResources;
import edu.ucdavis.gwt.gis.client.toolbar.Toolbar;
import edu.ucdavis.gwt.gis.client.toolbar.button.ToolbarItem;


public class ProjectQueryButton extends ToolbarItem {
		
	private Search myHost = null;
	
	public void setMyHost(Search h) {
		myHost = h;
	}

	public ProjectQueryButton() {}
		
	@Override
	public Image getIcon() {
		return new Image(GadgetResources.INSTANCE.querymap());
	}

	@Override
	public void onAdd(Toolbar toolbar) {}

	@Override
	public void onClick(ClickEvent event) {
		myHost.projectQuerySettings.show();
	}

	@Override
	public String getText() {
		return "Query Tool";
	}

}
