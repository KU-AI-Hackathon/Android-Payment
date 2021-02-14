package com.nextop.project.ku_ai_hackathon;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class BillingManager implements PurchasesUpdatedListener {

    //멤버 변수
    List<SkuDetails> mSkuDetailsList = null;

    // 필요 상수들.
    final String TAG = "IN-APP-BILLING";


    // 초기화 시 입력 받거나 생성되는 멤버 변수들.
    private BillingClient mBillingClient;
    private Activity mActivity;
    private ConsumeResponseListener mConsumResListnere;

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        // 결제에 성공한 경우.
        if( billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null )
        {

            Log.d( TAG, "결제에 성공했으며, 아래에 구매한 상품들이 나열될 것 입니다." );

            for( Purchase _pur : list )
            {
                Log.d( TAG, "결제에 대해 응답 받은 데이터 :"+ _pur.getOriginalJson() );
                mBillingClient.consumeAsync( _pur.getPurchaseToken(), mConsumResListnere );
            }
        }

        // 사용자가 결제를 취소한 경우.
        else if( billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED )
        {
            Log.d( TAG, "사용자에 의해 결제가 취소 되었습니다." );
        }

        // 그 외에 다른 결제 실패 이유.
        else
        {
            Log.d( TAG, "결제가 취소 되었습니다. 종료코드 : " + list );
        }
    }


    // 현재 접속 상태를 나타 냅니다.
    public enum connectStatusTypes { waiting, connected, fail, disconnected }
    public connectStatusTypes connectStatus = connectStatusTypes.waiting;



    // 생성자.
    public BillingManager( Activity _activity )
    {


        // 초기화 시 입력 받은 값들을 넣어 줍니다.
        mActivity = _activity;


        Log.d(TAG, "구글 결제 매니저를 초기화 하고 있습니다.");

        // 결제를 위한, 빌링 클라이언트를 생성합니다.
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();


        // 구글 플레이와 연결을 시도합니다.
        mBillingClient.startConnection(new BillingClientStateListener() {

            // 결제 작업 완료 가능한 상태.
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                // 접속이 성공한 경우, 처리.
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connectStatus = connectStatusTypes.connected;
                    Log.d(TAG, "구글 결제 서버에 접속을 성공하였습니다.");
                }

                // 접속이 실패한 경우, 처리.
                else {

                    connectStatus = connectStatusTypes.fail;
                    Log.d(TAG, "구글 결제 서버 접속에 실패하였습니다.\n오류코드:" + billingResult.getResponseCode());
                }

            }

            // 결제 작업 중, 구글 서버와 연결이 끊어진 상태.
            @Override
            public void onBillingServiceDisconnected() {

                connectStatus = connectStatusTypes.disconnected;
                Log.d(TAG, "구글 결제 서버와 접속이 끊어졌습니다.");

            }


        });


        mConsumResListnere = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                // 성공적으로 아이템을 소모한 경우.
                if( billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK )
                {
                    Log.d( TAG, "상품을 성공적으로 소모하였습니다. 소모된 상품=>" + s );
                    return;
                }

                // 성공적으로 아이템을 소모한 경우.
                else
                {
                    Log.d( TAG, "상품 소모에 실패하였습니다. 오류코드 ("+billingResult.getResponseCode()+"), 대상 상품 코드:" + s );
                    return;
                }
            }
        };

    }

    // 구입 가능한 상품의 리스트를 받아 오는 메소드 입니다.
    public void get_Sku_Detail_List()
    {


        // 구글의 상품 정보들의 ID를 만들어 줍니다.
        List<String> Sku_ID_List = new ArrayList<>();
        Sku_ID_List.add( "gwango_100" );


        // SkuDetailsParam 객체를 만들어 줍니다. (1회성 소모품에 대한)
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList( Sku_ID_List ).setType(BillingClient.SkuType.INAPP);


        // 비동기 상태로 앱의, 정보를 가지고 옵니다.
        mBillingClient.querySkuDetailsAsync(params.build() , new SkuDetailsResponseListener() {

                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        // 상품 정보를 가지고 오지 못한 경우, 오류를 반환하고 종료합니다.
                        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG, "상품 정보를 가지고 오던 중 오류를 만났습니다. 오류코드 : " + billingResult.getResponseCode());
                            return;
                        }

                        // 하나의 상품 정보도 가지고 오지 못했습니다.
                        if (list == null) {
                            Log.d(TAG, "상품 정보가 존재하지 않습니다.");
                            return;
                        }


                        // 응답 받은 데이터들의 숫자를 출력 합니다.
                        Log.d(TAG, "응답 받은 데이터 숫자:" + list.size());

                        // 받아 온 상품 정보를 차례로 호출 합니다.
                        for (int _sku_index = 0; _sku_index < list.size(); _sku_index++) {

                            // 해당 인덱스의 객체를 가지고 옵니다.
                            SkuDetails _skuDetail = list.get(_sku_index);

                            // 해당 인덱스의 상품 정보를 출력하도록 합니다.
                            Log.d(TAG, _skuDetail.getSku() + ": " + _skuDetail.getTitle() + ", " + _skuDetail.getPrice() + ", " + _skuDetail.getDescription());

                        }


                        // 받은 값을 멤버 변수로 저장합니다.
                        mSkuDetailsList = list;
                    }
                }
        );
    }

    public void purchase( SkuDetails skuDetails )
    {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        BillingResult responseCode = mBillingClient.launchBillingFlow(mActivity, flowParams);
    }
}
