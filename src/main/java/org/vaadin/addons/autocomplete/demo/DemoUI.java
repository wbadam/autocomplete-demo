package org.vaadin.addons.autocomplete.demo;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang3.text.WordUtils;
import org.vaadin.addons.autocomplete.AutocompleteExtension;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class DemoUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        // Planets
        Label planetLabel = new Label(
                "Simple text example with planets");
        TextField planetField = new TextField();

        AutocompleteExtension<String> planetExtension = new AutocompleteExtension<>(
                planetField);
        planetExtension.setSuggestionGenerator(this::suggestPlanet);

        planetField.addValueChangeListener(e -> {
            System.out.println(e.getOldValue() + " -> " + e.getValue());
        });

        // Users
        Label userLabel = new Label(
                "HTML suggestions with users' pictures and names");
        TextField userField = new TextField();
        userField.setWidth(250, Unit.PIXELS);

        AutocompleteExtension<DataSource.User> userSuggestion = new AutocompleteExtension<>(
                userField);
        userSuggestion.setSuggestionGenerator(this::suggestUsers,
                this::convertValueUser, this::convertCaptionUser);
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.addComponents(planetLabel, planetField, new Label(), userLabel,
                userField);

        setStyles();

        setContent(layout);
    }

    private List<String> suggestPlanet(String query, int cap) {
        return DataSource.getPlanets().stream()
                .filter(p -> p.toLowerCase().contains(query.toLowerCase()))
                .limit(cap).collect(Collectors.toList());
    }

    private List<DataSource.User> suggestUsers(String query, int cap) {
        return DataSource.getUsers().stream()
                .filter(user -> user.getName().contains(query.toLowerCase()))
                .limit(cap).collect(Collectors.toList());
    }

    private String convertValueUser(DataSource.User user) {
        return WordUtils.capitalizeFully(user.getName(), ' ');
    }

    private String convertCaptionUser(DataSource.User user, String query) {
        return "<div class='suggestion-container'>"
                + "<img src='" + user.getPicture() + "' class='userimage'>"
                + "<span class='username'>"
                + WordUtils.capitalizeFully(user.getName(), ' ').replaceAll("(?i)(" + query + ")", "<b>$1</b>")
                + "</span>"
                + "</div>";
    }

    private void setStyles() {
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".suggestion-container{"
                + "display:flex;"
                + "align-items:center;"
                + "padding-top:2px;"
                + "padding-bottom:2px;"
                + "}");
        styles.add(".userimage{border-radius: 50%;}");
        styles.add(".username{padding-left: 10px;}");
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = DemoUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

    }
}
