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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Route("")
public class ColorProblem extends VerticalLayout {

    private DesktopGridProDataView<SomeDto> dataView;
    private DesktopGridPro<SomeDto> grid;
    private GridMultiSelectionModel<SomeDto> selectionModel;


    public ColorProblem() {
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
    }


    public List<SomeDto> createData (int amount) {
        List<SomeDto> result = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            result.add(new SomeDto(i, "Position " + i, "Article " + i, BigDecimal.valueOf(i).multiply(new BigDecimal("0.2"))));
        }

        return result;
    }


}