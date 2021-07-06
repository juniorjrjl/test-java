package br.com.blz.testjava.api.mapper;

import br.com.blz.testjava.api.controller.request.insert.ProductInsertRequest;
import br.com.blz.testjava.api.controller.request.insert.ProductInventoryInsertRequest;
import br.com.blz.testjava.api.controller.request.insert.ProductWarehouseInsertRequest;
import br.com.blz.testjava.api.controller.request.update.ProductInventoryUpdateRequest;
import br.com.blz.testjava.api.controller.request.update.ProductUpdateRequest;
import br.com.blz.testjava.api.controller.request.update.ProductWarehouseUpdateRequest;
import br.com.blz.testjava.api.controller.response.inserted.ProductInsertedResponse;
import br.com.blz.testjava.api.controller.response.inserted.ProductInventoryInsertedResponse;
import br.com.blz.testjava.api.controller.response.search.ProductInventorySearchResponse;
import br.com.blz.testjava.api.controller.response.search.ProductSearchResponse;
import br.com.blz.testjava.api.controller.response.updated.ProductInventoryUpdatedResponse;
import br.com.blz.testjava.api.controller.response.updated.ProductUpdatedResponse;
import br.com.blz.testjava.domain.model.ProductModel;
import br.com.blz.testjava.domain.model.WarehouseModel;
import br.com.blz.testjava.domain.model.type.WarehousesType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-07-03T10:49:11-0300",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from kotlin-annotation-processing-gradle-1.5.20.jar, environment: Java 11.0.10 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl extends ProductMapper {

    @Override
    public ProductModel toModel(ProductInsertRequest request) {
        if ( request == null ) {
            return null;
        }

        List<WarehouseModel> warehouses = null;
        Long sku = null;
        String name = null;

        List<ProductWarehouseInsertRequest> warehouses1 = requestInventoryWarehouses( request );
        warehouses = productWarehouseInsertRequestListToWarehouseModelList( warehouses1 );
        sku = request.getSku();
        name = request.getName();

        ProductModel productModel = new ProductModel( sku, name, warehouses );

        return productModel;
    }

    @Override
    public ProductInsertedResponse toInsertedResponse(ProductModel model) {
        if ( model == null ) {
            return null;
        }

        Long sku = null;
        String name = null;

        sku = model.getSku();
        name = model.getName();

        ProductInventoryInsertedResponse inventory = buildInsertedInventory(model.getWarehouses());
        Boolean isMarketable = isMarketable(model.getWarehouses());

        ProductInsertedResponse productInsertedResponse = new ProductInsertedResponse( sku, name, inventory, isMarketable );

        return productInsertedResponse;
    }

    @Override
    public ProductModel toModel(ProductUpdateRequest request, long sku) {
        if ( request == null ) {
            return null;
        }

        List<WarehouseModel> warehouses = null;
        String name = null;
        if ( request != null ) {
            List<ProductWarehouseUpdateRequest> warehouses1 = requestInventoryWarehouses1( request );
            warehouses = productWarehouseUpdateRequestListToWarehouseModelList( warehouses1 );
            name = request.getName();
        }
        Long sku1 = null;
        sku1 = sku;

        ProductModel productModel = new ProductModel( sku1, name, warehouses );

        return productModel;
    }

    @Override
    public ProductUpdatedResponse toUpdatedResponse(ProductModel model) {
        if ( model == null ) {
            return null;
        }

        Long sku = null;
        String name = null;

        sku = model.getSku();
        name = model.getName();

        ProductInventoryUpdatedResponse inventory = buildUpdatedInventory(model.getWarehouses());
        Boolean isMarketable = isMarketable(model.getWarehouses());

        ProductUpdatedResponse productUpdatedResponse = new ProductUpdatedResponse( sku, name, inventory, isMarketable );

        return productUpdatedResponse;
    }

    @Override
    public ProductSearchResponse toSearchResponse(ProductModel model) {
        if ( model == null ) {
            return null;
        }

        Long sku = null;
        String name = null;

        sku = model.getSku();
        name = model.getName();

        ProductInventorySearchResponse inventory = buildFoundedInventory(model.getWarehouses());
        Boolean isMarketable = isMarketable(model.getWarehouses());

        ProductSearchResponse productSearchResponse = new ProductSearchResponse( sku, name, inventory, isMarketable );

        return productSearchResponse;
    }

    private List<ProductWarehouseInsertRequest> requestInventoryWarehouses(ProductInsertRequest productInsertRequest) {
        if ( productInsertRequest == null ) {
            return null;
        }
        ProductInventoryInsertRequest inventory = productInsertRequest.getInventory();
        if ( inventory == null ) {
            return null;
        }
        List<ProductWarehouseInsertRequest> warehouses = inventory.getWarehouses();
        if ( warehouses == null ) {
            return null;
        }
        return warehouses;
    }

    protected WarehouseModel productWarehouseInsertRequestToWarehouseModel(ProductWarehouseInsertRequest productWarehouseInsertRequest) {
        if ( productWarehouseInsertRequest == null ) {
            return null;
        }

        String locality = null;
        Long quantity = null;
        WarehousesType type = null;

        locality = productWarehouseInsertRequest.getLocality();
        quantity = productWarehouseInsertRequest.getQuantity();
        type = productWarehouseInsertRequest.getType();

        WarehouseModel warehouseModel = new WarehouseModel( locality, quantity, type );

        return warehouseModel;
    }

    protected List<WarehouseModel> productWarehouseInsertRequestListToWarehouseModelList(List<ProductWarehouseInsertRequest> list) {
        if ( list == null ) {
            return null;
        }

        List<WarehouseModel> list1 = new ArrayList<WarehouseModel>( list.size() );
        for ( ProductWarehouseInsertRequest productWarehouseInsertRequest : list ) {
            list1.add( productWarehouseInsertRequestToWarehouseModel( productWarehouseInsertRequest ) );
        }

        return list1;
    }

    private List<ProductWarehouseUpdateRequest> requestInventoryWarehouses1(ProductUpdateRequest productUpdateRequest) {
        if ( productUpdateRequest == null ) {
            return null;
        }
        ProductInventoryUpdateRequest inventory = productUpdateRequest.getInventory();
        if ( inventory == null ) {
            return null;
        }
        List<ProductWarehouseUpdateRequest> warehouses = inventory.getWarehouses();
        if ( warehouses == null ) {
            return null;
        }
        return warehouses;
    }

    protected WarehouseModel productWarehouseUpdateRequestToWarehouseModel(ProductWarehouseUpdateRequest productWarehouseUpdateRequest) {
        if ( productWarehouseUpdateRequest == null ) {
            return null;
        }

        String locality = null;
        Long quantity = null;
        WarehousesType type = null;

        locality = productWarehouseUpdateRequest.getLocality();
        quantity = productWarehouseUpdateRequest.getQuantity();
        type = productWarehouseUpdateRequest.getType();

        WarehouseModel warehouseModel = new WarehouseModel( locality, quantity, type );

        return warehouseModel;
    }

    protected List<WarehouseModel> productWarehouseUpdateRequestListToWarehouseModelList(List<ProductWarehouseUpdateRequest> list) {
        if ( list == null ) {
            return null;
        }

        List<WarehouseModel> list1 = new ArrayList<WarehouseModel>( list.size() );
        for ( ProductWarehouseUpdateRequest productWarehouseUpdateRequest : list ) {
            list1.add( productWarehouseUpdateRequestToWarehouseModel( productWarehouseUpdateRequest ) );
        }

        return list1;
    }
}
