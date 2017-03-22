package com.easyapps.teleprompter.presentation;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.easyapps.teleprompter.R;
import com.easyapps.teleprompter.application.LyricApplicationService;
import com.easyapps.teleprompter.domain.model.lyric.IConfigurationRepository;
import com.easyapps.teleprompter.domain.model.lyric.ILyricRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricFinder;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.teleprompter.infrastructure.persistence.lyric.AndroidPreferenceConfigurationRepository;
import com.easyapps.teleprompter.presentation.components.PlayableCustomAdapter;
import com.easyapps.teleprompter.query.model.lyric.ILyricFinder;

/**
 * List all files found by the app that contains lyrics to prompt.
 * Created by danielfmsouza on 22/03/2017.
 */

public class ListFilesFragment extends ListFragment implements ActivityCallback {

    boolean mDualPane;
    int mCurCheckPosition = 0;
    private Menu mOptionsMenu;
    private LyricApplicationService mAppService;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ILyricRepository lyricRepository = new AndroidFileSystemLyricRepository(getActivity());
        IConfigurationRepository configRepository = new AndroidPreferenceConfigurationRepository(getActivity());
        ILyricFinder lyricFinder = new AndroidFileSystemLyricFinder(getActivity());
        mAppService = new LyricApplicationService(lyricRepository, lyricFinder, configRepository);

        setListAdapter(new PlayableCustomAdapter(getActivity(), this, mAppService.getAllLyrics()));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
//        View detailsFrame = getActivity().findViewById(R.id.update_file_frag);
//        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;

//        if (mDualPane) {
//            // We can display everything in-place with fragments, so update
//            // the list to highlight the selected item and show the data.
//            getListView().setItemChecked(index, true);
//
//            // Check what fragment is currently shown, replace if needed.
//            UpdateFileFragment details = (UpdateFileFragment)
//                    getFragmentManager().findFragmentById(R.id.update_file_frag);
//            if (details == null || details.getShownIndex() != index) {
//                // Make new fragment to show this selection.
//                details = UpdateFileFragment.newInstance(index);
//
//                // Execute a transaction, replacing any existing fragment
//                // with this one inside the frame.
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                if (index == 0) {
////                    ft.replace(R.id.update_file_frag, details);
//                } else {
////                    ft.replace(R.id.a_item, details);
//                }
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                ft.commit();
//            }
//
//        } else {
//            // Otherwise we need to launch a new activity to display
//            // the dialog fragment with selected text.
//            Intent intent = new Intent();
//            intent.setClass(getActivity(), CreateLyricActivity.class);
//            intent.putExtra("index", index);
//            startActivity(intent);
//        }
    }

    /**
     * Show the trash button when called from some child component.
     */
    @Override
    public void showContent() {
        MenuItem deleteItemMenu = mOptionsMenu.getItem(0);
        deleteItemMenu.setVisible(true);
    }

    /**
     * Hide the trash button when called from some child component.
     */
    @Override
    public void hideContent() {
        MenuItem deleteItemMenu = mOptionsMenu.getItem(0);
        deleteItemMenu.setVisible(false);
    }
}
