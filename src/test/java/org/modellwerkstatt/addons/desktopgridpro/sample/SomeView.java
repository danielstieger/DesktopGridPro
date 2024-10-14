package org.modellwerkstatt.addons.desktopgridpro.sample;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.gridpro.EditColumnConfigurator;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import org.modellwerkstatt.addons.desktopgridpro.DesktopGridPro;
import org.modellwerkstatt.addons.desktopgridpro.DesktopGridProDataView;
import org.modellwerkstatt.addons.desktopgridpro.sample.SomeDto;

import java.math.BigDecimal;
import java.util.*;

@Route("someview")
public class SomeView extends VerticalLayout {

    private DesktopGridProDataView<SomeDto> dataView;
    private DesktopGridPro<SomeDto> grid;
    private GridMultiSelectionModel<SomeDto> selectionModel;


    public SomeView() {
        this.setSizeFull();

        configureGrid();
        grid.setSizeFull();
        this.add(grid);


        List<SomeDto> allData = createData(2000);
        int rowToSelect = 300;
        List<SomeDto> selection = allData.subList(rowToSelect, rowToSelect + 1);
        boolean selectionInData = dataView.setNewList(grid, allData, selection);

        LinkedHashSet<SomeDto> collectionAsSet = new LinkedHashSet<>(selection);
        selectionModel.deselectAll();
        selectionModel.updateSelection(collectionAsSet, Collections.emptySet());


        GridContextMenu<SomeDto> contextMenu = new GridContextMenu<>(grid);
        contextMenu.addItem("Context menu test", event -> { Notification.show("You clicked the context menu.", 5000, Notification.Position.TOP_CENTER); });



        grid.getElement().addEventListener("cell-edit-started", e -> {
            grid.disableGlobalEsc();

            int idx = grid.getRowToSelectWhileEdit(e.getEventData());
            if (idx > 0) {
                grid.deselectAll();
                grid.select(dataView.getItem(idx - 1));
            }
        });



        grid.getElement().addEventListener("cell-edit-stopped", e -> {
            grid.enableGlobalEsc();
        });




        /*
         * Open: The focusOnCell() is jumping when setting the selection to 1
         *       and the content is short (e.g. 20 items)
         */
        grid.scrollToIndex(rowToSelect);
        // grid.focusOnCell(dataView.getItem(rowToSelect), grid.getColumns().get(0));
        grid.focus();


        Button cancelButton = new Button("ESC", e -> {
            Notification.show("You clicked the esc button.", 5000, Notification.Position.TOP_CENTER);
        });

        ShortcutRegistration btnShortcut = Shortcuts.addShortcutListener(this, () -> {
            Notification.show("You triggered the esc button by HK.", 5000, Notification.Position.TOP_CENTER);
        }, Key.ESCAPE);
        btnShortcut.setEventPropagationAllowed(false);
        btnShortcut.setBrowserDefaultAllowed(false);

        this.add(cancelButton);
    }


    public void configureGrid() {
        dataView = new DesktopGridProDataView<>();

        grid = new DesktopGridPro<>();
        grid.setEditOnClick(true);
        grid.setEnterNextRow(true);
        grid.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_EDITABLE_CELLS);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setThemeName("dense");

        selectionModel = (GridMultiSelectionModel<SomeDto>) grid.getSelectionModel();

        // column posnum
        Grid.Column<SomeDto> col;
        String litPropName = "posNum";
        String template = "<span style=\"${item." + litPropName + "Style}\">${item." + litPropName + "}</span>";

        col = grid.addColumn(LitRenderer.<SomeDto>of(template).
                withProperty(litPropName, item -> {
                    return item.posNum;
                }).
                withProperty(litPropName + "Style", item -> {
                    return "color: #FF0000;";
                }));
        col.setHeader(litPropName);
        col.setResizable(true);

        // column name
        litPropName = "name";
        template = "<span style=\"${item." + litPropName + "Style}\">${item." + litPropName + "}</span>";

        col = grid.addColumn(LitRenderer.<SomeDto>of(template).
                withProperty(litPropName, item -> {
                    return item.name;
                }).
                withProperty(litPropName + "Style", item -> {
                    return "";
                }));
        col.setHeader(litPropName);
        col.setResizable(true);

        // column article
        litPropName = "article";
        template = "<span style=\"${item." + litPropName + "Style}\">${item." + litPropName + "}</span>";

        col = grid.addColumn(LitRenderer.<SomeDto>of(template).
                withProperty(litPropName, item -> {
                    return item.article;
                }).
                withProperty(litPropName + "Style", item -> {
                    return "";
                }));
        col.setHeader(litPropName);
        col.setResizable(true);

        // editable column value
        EditColumnConfigurator<SomeDto> editableCol = grid.addEditColumn(item -> item.value );
        editableCol.text((item, newValue) ->
        {
            try {
                item.value = new BigDecimal(newValue);
            } catch (Exception e) {
                Notification.show("Text not accepted! " + e.getMessage(), 4000, Notification.Position.TOP_END);
            }
        });
        col = editableCol.getColumn();
        col.setHeader("value");
        col.setResizable(true);
    }


    public List<SomeDto> createData (int amount) {
        List<SomeDto> result = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            result.add(new SomeDto(i, "Position " + i, "Article " + i, BigDecimal.valueOf(i).multiply(new BigDecimal("0.2"))));
        }

        return result;
    }


}