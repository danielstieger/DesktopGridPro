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
import org.modellwerkstatt.addons.desktopgridpro.DesktopGridPro;
import org.modellwerkstatt.addons.desktopgridpro.DesktopGridProDataView;
import org.modellwerkstatt.addons.desktopgridpro.sample.SomeDto;

import java.math.BigDecimal;
import java.util.*;

@Route("")
public class SomeView extends VerticalLayout {

    private DesktopGridProDataView<SomeDto> dataView;
    private DesktopGridPro<SomeDto> grid;
    private GridMultiSelectionModel<SomeDto> selectionModel;

    /* Open: GridPro/SelectionGrid speed improvements, mitigate round-tripping?
     *       js: in which cases is this.$server undefined?
     */

    /* Close: Should i rename the component SelectionGrid to DesktopGrid
     *  yes, rename it.
     * */


    public SomeView() {
        this.setSizeFull();

        /* Open: LUMO_HIGHLIGHT_EDITABLE_CELLS not working, although present in gridglobal.css */
        configureGrid();
        grid.setSizeFull();
        this.add(grid);


        /* Solved: I assume using grid.setItems() is ressource optimal here */
        List<SomeDto> allData = createData(20);

        /* Solved: Selection from 0 not working - https://github.com/vaadin-component-factory/selection-grid-flow/pull/39 */
        List<SomeDto> selection = allData.subList(3, 4);
        boolean selectionInData = dataView.setNewList(grid, allData, selection);

        /* Solved: selection via list is not possible, right? */
        LinkedHashSet<SomeDto> collectionAsSet = new LinkedHashSet<>(selection);
        selectionModel.deselectAll();
        selectionModel.updateSelection(collectionAsSet, Collections.emptySet());


        /* Solved: onContextMenu() does the selection */
        GridContextMenu<SomeDto> contextMenu = new GridContextMenu<>(grid);
        contextMenu.addItem("Context menu test", event -> { Notification.show("You clicked the context menu.", 5000, Notification.Position.TOP_CENTER); });


        // Solved: select next item on enter / shift-tab
        // grid.
        grid.getElement().addEventListener("cell-edit-started", e -> {
            grid.disableGlobalEsc();

            int idx = grid.getRowToSelectWhileEdit(e.getEventData());
            if (idx > 0) {
                grid.deselectAll();
                grid.select(dataView.getItem(idx - 1));
            }
        });


        // Open: grid.getEditor().addCancelListener() is not working.
        grid.getElement().addEventListener("cell-edit-stopped", e -> {
            grid.enableGlobalEsc();
        });


        /* Open: The cell is editable, but visualization is not correct
         *       OR: how could i get the cell into edit mode?
         *
         * Open: The focusOnCell() is jumping when setting the selection to 1
         *       and the content is short (e.g. 20 items)
         */

        // funktioniert besser ...

        grid.scrollToIndex(5);
        grid.focusOnCell(dataView.getItem(5), grid.getColumns().get(0));

        // grid.focus();


        /* Open: how to do validation in editable columns, check text() below */


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
            result.add(new SomeDto(i, "Position " + i, "Article 0", BigDecimal.valueOf(i).multiply(new BigDecimal("0.2"))));
        }

        return result;
    }


}