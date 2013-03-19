package gov.ca.ceres.maps.client;

import com.google.gwt.user.client.ui.Image;

import edu.ucdavis.gwt.gis.client.toolbar.button.QueryButton;
import edu.ucdavis.gwt.gis.client.toolbar.menu.ToolbarMenu;

public class ProjectQueryMenu extends ToolbarMenu {
	
	public ProjectQueryButton button = new ProjectQueryButton();
	
	public ProjectQueryMenu() {
		addItem(button);
	}
	
	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	public String getText() {
		return "Query";
	}
}
