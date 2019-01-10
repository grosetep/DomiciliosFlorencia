package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces;

import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.CartRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ChangeStatusOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.CreateOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.RegisterDeviceRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingBudgetRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UpdateLocationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UpdateShoppingCartRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UserOperationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.BudgetResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.ConfigurationResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.CreateOrderResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetCartResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetContactsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetOrdersResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetPaymentMethodResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetProductsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetShippingAddressResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.HelpTextsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.OrderDetailResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.RatesResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.UserResponse;

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
    Call<GenericResponse> shoppingCart(@Body CartRequest cart);

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

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("budget")
    Call<BudgetResponse> budget(@Body ShippingBudgetRequest request);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("createShippingOrder")
    Call<GenericResponse> createShippingOrder(@Body ShippingOrderRequest request);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("shippingAddressOperation")
    Call<GenericResponse> shippingAddressOperation(@Body UpdateLocationRequest request);


    @GET("getInitialRate/{id_country}")
        Call<RatesResponse> getInitialRate(@Path("id_country") String id_country);

    @GET("getHelpTexts/{id_country}")
    Call<HelpTextsResponse> getHelpTexts(@Path("id_country") String id_country);
}