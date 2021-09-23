package com.yorder.shop.databinding;
import com.yorder.shop.R;
import com.yorder.shop.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentProductCustomisationBindingImpl extends FragmentProductCustomisationBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.BottomLayout, 1);
        sViewsWithIds.put(R.id.tvline, 2);
        sViewsWithIds.put(R.id.toparea, 3);
        sViewsWithIds.put(R.id.imBack, 4);
        sViewsWithIds.put(R.id.productNameTv, 5);
        sViewsWithIds.put(R.id.productVariatinTVList, 6);
        sViewsWithIds.put(R.id.scrollViewcu, 7);
        sViewsWithIds.put(R.id.productPriceTv, 8);
        sViewsWithIds.put(R.id.tvNameLine, 9);
        sViewsWithIds.put(R.id.kVariantTv, 10);
        sViewsWithIds.put(R.id.variantClearButton, 11);
        sViewsWithIds.put(R.id.productVariationTvLine, 12);
        sViewsWithIds.put(R.id.variantsRecyclerView, 13);
        sViewsWithIds.put(R.id.dividerVariation, 14);
        sViewsWithIds.put(R.id.kAddOnTv, 15);
        sViewsWithIds.put(R.id.kAddOnTvoptional, 16);
        sViewsWithIds.put(R.id.addOnsClearButton, 17);
        sViewsWithIds.put(R.id.addOnsRecyclerView, 18);
        sViewsWithIds.put(R.id.dividerAddons, 19);
        sViewsWithIds.put(R.id.kSpecialInstructionTV, 20);
        sViewsWithIds.put(R.id.etAddSpecialInstruction, 21);
        sViewsWithIds.put(R.id.addToCartButton, 22);
        sViewsWithIds.put(R.id.lyListProductAddButton, 23);
        sViewsWithIds.put(R.id.tvPLRemove, 24);
        sViewsWithIds.put(R.id.tvPLQty, 25);
        sViewsWithIds.put(R.id.tvPLPlus, 26);
    }
    // views
    @NonNull
    private final androidx.coordinatorlayout.widget.CoordinatorLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentProductCustomisationBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 27, sIncludes, sViewsWithIds));
    }
    private FragmentProductCustomisationBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[1]
            , (android.widget.Button) bindings[17]
            , (androidx.recyclerview.widget.RecyclerView) bindings[18]
            , (android.widget.Button) bindings[22]
            , (android.widget.TextView) bindings[19]
            , (android.widget.TextView) bindings[14]
            , (android.widget.EditText) bindings[21]
            , (android.widget.ImageView) bindings[4]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[16]
            , (android.widget.TextView) bindings[20]
            , (android.widget.TextView) bindings[10]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[23]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[12]
            , (androidx.core.widget.NestedScrollView) bindings[7]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[3]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[26]
            , (android.widget.TextView) bindings[25]
            , (android.widget.TextView) bindings[24]
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[11]
            , (androidx.recyclerview.widget.RecyclerView) bindings[13]
            );
        this.mboundView0 = (androidx.coordinatorlayout.widget.CoordinatorLayout) bindings[0];
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