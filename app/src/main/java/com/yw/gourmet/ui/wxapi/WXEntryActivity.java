package com.yw.gourmet.ui.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yw.gourmet.Constant;
import com.yw.gourmet.utils.ToastUtils;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
 		api.registerApp(Constant.APP_ID);

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			ToastUtils.showSingleToast("分享成功");
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			ToastUtils.showSingleToast("取消分享");
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			ToastUtils.showSingleToast("APP出错了哟,请重新安装APP后重试");
			break;
		default:
			break;
		}
		finish();

	}
}