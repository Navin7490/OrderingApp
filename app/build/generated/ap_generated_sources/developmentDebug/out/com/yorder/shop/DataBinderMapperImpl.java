package com.yorder.shop;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.yorder.shop.databinding.DialoagChangephonenumberBindingImpl;
import com.yorder.shop.databinding.DialogAddstudentBindingImpl;
import com.yorder.shop.databinding.DialogUpdateBindingImpl;
import com.yorder.shop.databinding.FragmentCartBindingImpl;
import com.yorder.shop.databinding.FragmentForgotpasswordBindingImpl;
import com.yorder.shop.databinding.FragmentHelpBindingImpl;
import com.yorder.shop.databinding.FragmentLoginWithphoneBindingImpl;
import com.yorder.shop.databinding.FragmentOrdersBindingImpl;
import com.yorder.shop.databinding.FragmentProductCustomisationBindingImpl;
import com.yorder.shop.databinding.FragmentProductsListBindingImpl;
import com.yorder.shop.databinding.FragmentProfileBindingImpl;
import com.yorder.shop.databinding.FragmentRegistrationBindingImpl;
import com.yorder.shop.databinding.FragmentSellerInfoBindingImpl;
import com.yorder.shop.databinding.FragmentSellersBindingImpl;
import com.yorder.shop.databinding.FragmentSigninBindingImpl;
import com.yorder.shop.databinding.FragmentSplashBindingImpl;
import com.yorder.shop.databinding.FragmentVariationsOrAddonsBindingImpl;
import com.yorder.shop.databinding.FragmentVerificationBindingImpl;
import com.yorder.shop.databinding.ItemCartAdapterBindingImpl;
import com.yorder.shop.databinding.ItemProductAddOnsAdapterBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_DIALOAGCHANGEPHONENUMBER = 1;

  private static final int LAYOUT_DIALOGADDSTUDENT = 2;

  private static final int LAYOUT_DIALOGUPDATE = 3;

  private static final int LAYOUT_FRAGMENTCART = 4;

  private static final int LAYOUT_FRAGMENTFORGOTPASSWORD = 5;

  private static final int LAYOUT_FRAGMENTHELP = 6;

  private static final int LAYOUT_FRAGMENTLOGINWITHPHONE = 7;

  private static final int LAYOUT_FRAGMENTORDERS = 8;

  private static final int LAYOUT_FRAGMENTPRODUCTCUSTOMISATION = 9;

  private static final int LAYOUT_FRAGMENTPRODUCTSLIST = 10;

  private static final int LAYOUT_FRAGMENTPROFILE = 11;

  private static final int LAYOUT_FRAGMENTREGISTRATION = 12;

  private static final int LAYOUT_FRAGMENTSELLERINFO = 13;

  private static final int LAYOUT_FRAGMENTSELLERS = 14;

  private static final int LAYOUT_FRAGMENTSIGNIN = 15;

  private static final int LAYOUT_FRAGMENTSPLASH = 16;

  private static final int LAYOUT_FRAGMENTVARIATIONSORADDONS = 17;

  private static final int LAYOUT_FRAGMENTVERIFICATION = 18;

  private static final int LAYOUT_ITEMCARTADAPTER = 19;

  private static final int LAYOUT_ITEMPRODUCTADDONSADAPTER = 20;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(20);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.dialoag_changephonenumber, LAYOUT_DIALOAGCHANGEPHONENUMBER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.dialog_addstudent, LAYOUT_DIALOGADDSTUDENT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.dialog_update, LAYOUT_DIALOGUPDATE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_cart, LAYOUT_FRAGMENTCART);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_forgotpassword, LAYOUT_FRAGMENTFORGOTPASSWORD);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_help, LAYOUT_FRAGMENTHELP);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_login_withphone, LAYOUT_FRAGMENTLOGINWITHPHONE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_orders, LAYOUT_FRAGMENTORDERS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_product_customisation, LAYOUT_FRAGMENTPRODUCTCUSTOMISATION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_products_list, LAYOUT_FRAGMENTPRODUCTSLIST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_profile, LAYOUT_FRAGMENTPROFILE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_registration, LAYOUT_FRAGMENTREGISTRATION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_seller_info, LAYOUT_FRAGMENTSELLERINFO);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_sellers, LAYOUT_FRAGMENTSELLERS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_signin, LAYOUT_FRAGMENTSIGNIN);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_splash, LAYOUT_FRAGMENTSPLASH);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_variations_or_addons, LAYOUT_FRAGMENTVARIATIONSORADDONS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.fragment_verification, LAYOUT_FRAGMENTVERIFICATION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.item_cart_adapter, LAYOUT_ITEMCARTADAPTER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.yorder.shop.R.layout.item_product_add_ons_adapter, LAYOUT_ITEMPRODUCTADDONSADAPTER);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_DIALOAGCHANGEPHONENUMBER: {
          if ("layout/dialoag_changephonenumber_0".equals(tag)) {
            return new DialoagChangephonenumberBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for dialoag_changephonenumber is invalid. Received: " + tag);
        }
        case  LAYOUT_DIALOGADDSTUDENT: {
          if ("layout/dialog_addstudent_0".equals(tag)) {
            return new DialogAddstudentBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for dialog_addstudent is invalid. Received: " + tag);
        }
        case  LAYOUT_DIALOGUPDATE: {
          if ("layout/dialog_update_0".equals(tag)) {
            return new DialogUpdateBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for dialog_update is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTCART: {
          if ("layout/fragment_cart_0".equals(tag)) {
            return new FragmentCartBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_cart is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTFORGOTPASSWORD: {
          if ("layout/fragment_forgotpassword_0".equals(tag)) {
            return new FragmentForgotpasswordBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_forgotpassword is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTHELP: {
          if ("layout/fragment_help_0".equals(tag)) {
            return new FragmentHelpBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_help is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTLOGINWITHPHONE: {
          if ("layout/fragment_login_withphone_0".equals(tag)) {
            return new FragmentLoginWithphoneBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_login_withphone is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTORDERS: {
          if ("layout/fragment_orders_0".equals(tag)) {
            return new FragmentOrdersBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_orders is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTPRODUCTCUSTOMISATION: {
          if ("layout/fragment_product_customisation_0".equals(tag)) {
            return new FragmentProductCustomisationBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_product_customisation is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTPRODUCTSLIST: {
          if ("layout/fragment_products_list_0".equals(tag)) {
            return new FragmentProductsListBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_products_list is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTPROFILE: {
          if ("layout/fragment_profile_0".equals(tag)) {
            return new FragmentProfileBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_profile is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTREGISTRATION: {
          if ("layout/fragment_registration_0".equals(tag)) {
            return new FragmentRegistrationBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_registration is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTSELLERINFO: {
          if ("layout/fragment_seller_info_0".equals(tag)) {
            return new FragmentSellerInfoBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_seller_info is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTSELLERS: {
          if ("layout/fragment_sellers_0".equals(tag)) {
            return new FragmentSellersBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_sellers is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTSIGNIN: {
          if ("layout/fragment_signin_0".equals(tag)) {
            return new FragmentSigninBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_signin is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTSPLASH: {
          if ("layout/fragment_splash_0".equals(tag)) {
            return new FragmentSplashBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_splash is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTVARIATIONSORADDONS: {
          if ("layout/fragment_variations_or_addons_0".equals(tag)) {
            return new FragmentVariationsOrAddonsBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_variations_or_addons is invalid. Received: " + tag);
        }
        case  LAYOUT_FRAGMENTVERIFICATION: {
          if ("layout/fragment_verification_0".equals(tag)) {
            return new FragmentVerificationBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for fragment_verification is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMCARTADAPTER: {
          if ("layout/item_cart_adapter_0".equals(tag)) {
            return new ItemCartAdapterBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_cart_adapter is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMPRODUCTADDONSADAPTER: {
          if ("layout/item_product_add_ons_adapter_0".equals(tag)) {
            return new ItemProductAddOnsAdapterBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_product_add_ons_adapter is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(1);

    static {
      sKeys.put(0, "_all");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(20);

    static {
      sKeys.put("layout/dialoag_changephonenumber_0", com.yorder.shop.R.layout.dialoag_changephonenumber);
      sKeys.put("layout/dialog_addstudent_0", com.yorder.shop.R.layout.dialog_addstudent);
      sKeys.put("layout/dialog_update_0", com.yorder.shop.R.layout.dialog_update);
      sKeys.put("layout/fragment_cart_0", com.yorder.shop.R.layout.fragment_cart);
      sKeys.put("layout/fragment_forgotpassword_0", com.yorder.shop.R.layout.fragment_forgotpassword);
      sKeys.put("layout/fragment_help_0", com.yorder.shop.R.layout.fragment_help);
      sKeys.put("layout/fragment_login_withphone_0", com.yorder.shop.R.layout.fragment_login_withphone);
      sKeys.put("layout/fragment_orders_0", com.yorder.shop.R.layout.fragment_orders);
      sKeys.put("layout/fragment_product_customisation_0", com.yorder.shop.R.layout.fragment_product_customisation);
      sKeys.put("layout/fragment_products_list_0", com.yorder.shop.R.layout.fragment_products_list);
      sKeys.put("layout/fragment_profile_0", com.yorder.shop.R.layout.fragment_profile);
      sKeys.put("layout/fragment_registration_0", com.yorder.shop.R.layout.fragment_registration);
      sKeys.put("layout/fragment_seller_info_0", com.yorder.shop.R.layout.fragment_seller_info);
      sKeys.put("layout/fragment_sellers_0", com.yorder.shop.R.layout.fragment_sellers);
      sKeys.put("layout/fragment_signin_0", com.yorder.shop.R.layout.fragment_signin);
      sKeys.put("layout/fragment_splash_0", com.yorder.shop.R.layout.fragment_splash);
      sKeys.put("layout/fragment_variations_or_addons_0", com.yorder.shop.R.layout.fragment_variations_or_addons);
      sKeys.put("layout/fragment_verification_0", com.yorder.shop.R.layout.fragment_verification);
      sKeys.put("layout/item_cart_adapter_0", com.yorder.shop.R.layout.item_cart_adapter);
      sKeys.put("layout/item_product_add_ons_adapter_0", com.yorder.shop.R.layout.item_product_add_ons_adapter);
    }
  }
}
