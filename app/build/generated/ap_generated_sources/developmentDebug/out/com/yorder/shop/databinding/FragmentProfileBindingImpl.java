package com.yorder.shop.databinding;
import com.yorder.shop.R;
import com.yorder.shop.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentProfileBindingImpl extends FragmentProfileBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.profileViewFlipper, 1);
        sViewsWithIds.put(R.id.areaProfile, 2);
        sViewsWithIds.put(R.id.ImProfile, 3);
        sViewsWithIds.put(R.id.vGuide, 4);
        sViewsWithIds.put(R.id.kFirstNameTV, 5);
        sViewsWithIds.put(R.id.tvfirstName, 6);
        sViewsWithIds.put(R.id.kLastNameTV, 7);
        sViewsWithIds.put(R.id.tvLastName, 8);
        sViewsWithIds.put(R.id.kEmailIdTV, 9);
        sViewsWithIds.put(R.id.tvEmail, 10);
        sViewsWithIds.put(R.id.kPhoneNumberTV, 11);
        sViewsWithIds.put(R.id.tvPhone, 12);
        sViewsWithIds.put(R.id.kAddressrTV, 13);
        sViewsWithIds.put(R.id.tvAddress, 14);
        sViewsWithIds.put(R.id.tvViewQRCode, 15);
        sViewsWithIds.put(R.id.dividerAddress, 16);
        sViewsWithIds.put(R.id.tvHelpAndSupport, 17);
        sViewsWithIds.put(R.id.tvRate, 18);
        sViewsWithIds.put(R.id.tvappVersion, 19);
        sViewsWithIds.put(R.id.scrollView, 20);
        sViewsWithIds.put(R.id.ImProfileE, 21);
        sViewsWithIds.put(R.id.TILFirstName, 22);
        sViewsWithIds.put(R.id.etFirstname, 23);
        sViewsWithIds.put(R.id.TILLastName, 24);
        sViewsWithIds.put(R.id.etLastName, 25);
        sViewsWithIds.put(R.id.TILEmailId, 26);
        sViewsWithIds.put(R.id.etEmailId, 27);
        sViewsWithIds.put(R.id.TILPhoneNumber, 28);
        sViewsWithIds.put(R.id.etPhone, 29);
        sViewsWithIds.put(R.id.TILHouseNo, 30);
        sViewsWithIds.put(R.id.etHouseNo, 31);
        sViewsWithIds.put(R.id.TILSocietyAppartment, 32);
        sViewsWithIds.put(R.id.etSocietyAppartment, 33);
        sViewsWithIds.put(R.id.TILArea, 34);
        sViewsWithIds.put(R.id.etArea, 35);
        sViewsWithIds.put(R.id.TILCityState, 36);
        sViewsWithIds.put(R.id.etCityState, 37);
        sViewsWithIds.put(R.id.TILPinCode, 38);
        sViewsWithIds.put(R.id.etPinCode, 39);
        sViewsWithIds.put(R.id.btnUpdateProfile, 40);
        sViewsWithIds.put(R.id.ccp, 41);
        sViewsWithIds.put(R.id.progressbar, 42);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentProfileBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 43, sIncludes, sViewsWithIds));
    }
    private FragmentProfileBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (de.hdodenhof.circleimageview.CircleImageView) bindings[3]
            , (de.hdodenhof.circleimageview.CircleImageView) bindings[21]
            , (com.google.android.material.textfield.TextInputLayout) bindings[34]
            , (com.google.android.material.textfield.TextInputLayout) bindings[36]
            , (com.google.android.material.textfield.TextInputLayout) bindings[26]
            , (com.google.android.material.textfield.TextInputLayout) bindings[22]
            , (com.google.android.material.textfield.TextInputLayout) bindings[30]
            , (com.google.android.material.textfield.TextInputLayout) bindings[24]
            , (com.google.android.material.textfield.TextInputLayout) bindings[28]
            , (com.google.android.material.textfield.TextInputLayout) bindings[38]
            , (com.google.android.material.textfield.TextInputLayout) bindings[32]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[2]
            , (android.widget.Button) bindings[40]
            , (com.hbb20.CountryCodePicker) bindings[41]
            , (android.widget.TextView) bindings[16]
            , (com.google.android.material.textfield.TextInputEditText) bindings[35]
            , (com.google.android.material.textfield.TextInputEditText) bindings[37]
            , (com.google.android.material.textfield.TextInputEditText) bindings[27]
            , (com.google.android.material.textfield.TextInputEditText) bindings[23]
            , (com.google.android.material.textfield.TextInputEditText) bindings[31]
            , (com.google.android.material.textfield.TextInputEditText) bindings[25]
            , (com.google.android.material.textfield.TextInputEditText) bindings[29]
            , (com.google.android.material.textfield.TextInputEditText) bindings[39]
            , (com.google.android.material.textfield.TextInputEditText) bindings[33]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[7]
            , (android.widget.TextView) bindings[11]
            , (android.widget.ViewFlipper) bindings[1]
            , (androidx.cardview.widget.CardView) bindings[42]
            , (androidx.core.widget.NestedScrollView) bindings[20]
            , (android.widget.TextView) bindings[14]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[17]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[18]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[19]
            , (android.widget.TextView) bindings[6]
            , (androidx.constraintlayout.widget.Guideline) bindings[4]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}