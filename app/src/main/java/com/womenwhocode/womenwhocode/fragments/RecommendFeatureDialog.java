package com.womenwhocode.womenwhocode.fragments;

import com.womenwhocode.womenwhocode.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by zassmin on 11/4/15.
 */
public class RecommendFeatureDialog extends DialogFragment {
    private Toolbar toolbar;
    private EditText mEditText;

    public RecommendFeatureDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RecommendFeatureDialog newInstance() {
        return new RecommendFeatureDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_recommend, container);
        toolbar = (Toolbar) view.findViewById(R.id.tbSuggestNewTopic);
        toolbar.inflateMenu(R.menu.menu_add_post_fragment);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.etRecommendation);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        MenuItem save = toolbar.getMenu().getItem(0); // position of id save

        save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.save) {
                    onSave();
                    return true;
                }
                return false;
            }
        });
    }

    private void onSave() {
        RecommendFeatureDialogListener listener = (RecommendFeatureDialogListener) getActivity();
        listener.onFinishEditDialog(mEditText.getText().toString());
        dismiss();
    }

    public interface RecommendFeatureDialogListener {
        void onFinishEditDialog(String inputText);
    }
//
//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        if (EditorInfo.IME_ACTION_DONE == actionId) {
//            RecommendFeatureDialogListener listener = (RecommendFeatureDialogListener) getActivity();
//            listener.onFinishEditDialog(v.getText().toString());
//            dismiss();
//            return true;
//        }
//        return false;
//    }
}
