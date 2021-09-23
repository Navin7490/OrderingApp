package com.yorder.shop.databinding;
import com.yorder.shop.R;
import com.yorder.shop.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentCartBindingImpl extends FragmentCartBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.scrollView, 1);
        sViewsWithIds.put(R.id.ImCartShopLogo, 2);
        sViewsWithIds.put(R.id.tvCarShopName, 3);
        sViewsWithIds.put(R.id.tvCartShopDetail, 4);
        sViewsWithIds.put(R.id.addButton, 5);
        sViewsWithIds.put(R.id.areaCard, 6);
        sViewsWithIds.put(R.id.recyclerViewcart, 7);
        sViewsWithIds.put(R.id.total, 8);
        sViewsWithIds.put(R.id.tvCartAllTotalPrice, 9);
        sViewsWithIds.put(R.id.kSpecialInstructionTV, 10);
        sViewsWithIds.put(R.id.etAddSpecialInstruction, 11);
        sViewsWithIds.put(R.id.btnCartProceedToPayFloating, 12);
        sViewsWithIds.put(R.id.progressbar, 13);
        sViewsWithIds.put(R.id.imCartEmpty, 14);
        sViewsWithIds.put(R.id.tvCartIsEmpty, 15);
        sViewsWithIds.put(R.id.btnCartContinueShopping, 16);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentCartBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 17, sIncludes, sViewsWithIds));
    }
    private FragmentCartBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (de.hdodenhof.circleimageview.CircleImageView) bindings[2]
            , (android.widget.TextView) bindings[5]
            , (androidx.cardview.widget.CardView) bindings[6]
            , (android.widget.Button) bindings[16]
            , (android.widget.Button) bindings[12]
            , (android.widget.EditText) bindings[11]
            , (android.widget.ImageView) bindings[14]
            , (android.widget.TextView) bindings[10]
            , (androidx.cardview.widget.CardView) bindings[13]
            , (androidx.recyclerview.widget.RecyclerView) bindings[7]
            , (androidx.core.widget.NestedScrollView) bindings[1]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[0]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[3]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[15]
            , (android.widget.TextView) bindings[4]
            );
        this.top.setTag(null);
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