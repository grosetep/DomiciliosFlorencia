package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces;



import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.AddProductRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ChangeStatusOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.CreateOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.RegisterDeviceRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingBudgetRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UpdateLocationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UpdateShoppingCartRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UserOperationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.AddressOperationResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.BudgetResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.CategoryResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.ClassificationsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.ConfigurationResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.CreateOrderResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetCartResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetContactsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetOrdersPurchaseResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetOrdersResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetPaymentMethodResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetProductsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetShippingAddressResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetVariantAdditionalResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.HelpTextsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.LookForProductsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.MerchantsByServiceResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.OrderDetailPurchaseResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.OrderDetailResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.RatesResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.StringResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.UserResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.lookForStoresResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by administrator on 19/07/2017.
 */
public interface WebServicesInterface {
    @GET("getProducts/{start}/{end}")
    Call<GetProductsResponse> getAllProducts(
            @Path("start") String start, @Path("end") String end);

    @GET("getLocationsByUser/{id_user}/{type_query}")
    Call<GetShippingAddressResponse> getLocationsByUser(
            @Path("id_user") String id_user, @Path("type_query") String type_query);

    @GET("getContactsByUser/{id_user}")
    Call<GetContactsResponse> getContactsByUser(
            @Path("id_user") String id_user);

    @GET("getPaymentMethods")
    Call<GetPaymentMethodResponse> getPaymentMethods();

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("cartOperation")
    Call<GenericResponse> shoppingCart(@Body AddProductRequest cart);

    @GET("getShoppingCart/{id_user}")
    Call<GetCartResponse> getShoppingCart(
            @Path("id_user") String id_user);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("createOrder")
    Call<CreateOrderResponse> createOrder(@Body CreateOrderRequest order);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("updateShoppingCart")
    Call<GenericResponse> updateShoppingCart(@Body UpdateShoppingCartRequest request);

    @GET("getOrders/{id_user}/{id_profile}/{start}/{end}/{days}")
    Call<GetOrdersResponse> getOrders(
            @Path("id_user") String id_user, @Path("id_profile") String id_profile, @Path("start") int start, @Path("end") int end, @Path("days") String days);

    @GET("getDeliverManOrders/{id_user}/{id_profile}/{type_query}/{start}/{end}/{days}")
    Call<GetOrdersResponse> getDeliverManOrders(
            @Path("id_user") String id_user, @Path("id_profile") String id_profile, @Path("type_query") String type_query, @Path("start") int start, @Path("end") int end, @Path("days") String days);

    @GET("getDeliverManOrdersPurchase/{id_user}/{id_profile}/{type_query}/{start}/{end}/{days}")
    Call<GetOrdersPurchaseResponse> getDeliverManOrdersPurchase(
            @Path("id_user") String id_user, @Path("id_profile") String id_profile, @Path("type_query") String type_query, @Path("start") int start, @Path("end") int end, @Path("days") String days);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("userOperation")
    Call<UserResponse> userOperation(@Body UserOperationRequest request);

    @GET("getConfiguration")
    Call<ConfigurationResponse> getConfiguration();

    @GET("recoveryPassword/{email}/{newPassword}/{encrypted}")
    Call<GenericResponse> recoveryPassword(
            @Path("email") String email, @Path("newPassword") String newPassword, @Path("encrypted") String encrypted);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("changeStatusOrder")
    Call<GenericResponse> ChangeStatusOrder(@Body ChangeStatusOrderRequest request);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("registerDevice")
    Call<GenericResponse> registerDevice(@Body RegisterDeviceRequest request);

    @GET("getDetailOrder/{id_order}")
    Call<OrderDetailResponse> getDetailOrder(
            @Path("id_order") String id_order);

    @GET("getDetailOrderPurchase/{id_order}")
    Call<OrderDetailPurchaseResponse> getDetailOrderPurchase(
            @Path("id_order") String id_order);

    @GET("getVariantsAditionals/{id_product}")
    Call<GetVariantAdditionalResponse> getVariantsXProduct(@Path("id_product") String id_product);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("budget")
    Call<BudgetResponse> budget(@Body ShippingBudgetRequest request);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("createShippingOrder")
    Call<GenericResponse> createShippingOrder(@Body ShippingOrderRequest request);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("shippingAddressOperation")
    Call<AddressOperationResponse> shippingAddressOperation(@Body UpdateLocationRequest request);

    @GET("getCategories/{id_merchant}")
    Call<CategoryResponse> getCategories(@Path("id_merchant") String id_merchant);

    @GET("getInitialRate/{id_country}")
    Call<RatesResponse> getInitialRate(@Path("id_country") String id_country);

    @GET("getHelpTexts/{id_country}")
    Call<HelpTextsResponse> getHelpTexts(@Path("id_country") String id_country);

    /*inicio cambios para domicilios*/
    @GET("getClassificationsByService/{type_service}")
    Call<ClassificationsResponse> getClassificationsByService(@Path("type_service") String type_service);

    @GET("getMerchantsByService/{type_service}/{classificationKey}/{id_country}/{start}/{end}")
    Call<MerchantsByServiceResponse> getMerchantsByService(@Path("type_service") String type_service,
                                                           @Path("classificationKey") String classificationKey,
                                                           @Path("id_country") String id_country,
                                                           @Path("start") int start,
                                                           @Path("end") int end);

    @GET("lookForStores/{filter}/{service_key}/{id_country}/{start}/{end}")
    Call<lookForStoresResponse> lookForStores(@Path("filter") String filter, @Path("service_key") String service_key,
                                              @Path("id_country") String id_country,
                                              @Path("start") int start, @Path("end") int end);

    @GET("executeLookForProducts/{filter}/{service_key}/{id_country}/{start}/{end}")
    Call<LookForProductsResponse> executeLookForProducts(@Path("filter") String filter, @Path("service_key") String service_key,
                                                         @Path("id_country") String id_country,
                                                         @Path("start") int start, @Path("end") int end);

    @GET("getExistingMerchantOnCart/{id_user}")
    Call<StringResponse> getExistingMerchantOnCart(@Path("id_user") String id_user);


    /*fin cambios para domicilios*/
}
