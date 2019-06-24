package com.easyapps.singerpro.presentation.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.application.command.AddLyricCommand;
import com.easyapps.singerpro.application.command.UpdateLyricCommand;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * A simple {@link Fragment} subclass the holds the creation and edition of a Lyric
 */
public class MaintainLyricFragment extends Fragment {
    private Operation mOperation = Operation.NEW_LYRIC;
    private Lyric mLyric;
    private OnSaveItemListener mListener;
    private boolean mIsTempFileUsed;

    @Inject
    LyricApplicationService mAppService;

    public MaintainLyricFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            try {
                String tempLyricName = getFileNameContent() + mAppService.getTempLyricName();
                mAppService.addLyric(new AddLyricCommand(tempLyricName, getTextContent(),
                        getSongNumberContent()));
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean saveLyricFile() {
        boolean savedSuccessfully = false;

        String lyricName = getFileNameContent();
        String lyricNumber = getSongNumberContent();
        String lyricContent = getTextContent();

        boolean valid = validateRequiredFields(lyricName, lyricNumber, lyricContent);
        if (valid) {
            try {
                if (mOperation == Operation.EDIT_LYRIC) {
                    updateLyric(lyricName, lyricNumber);
                } else {
                    addLyric(lyricName, lyricNumber);
                }

                mLyric = tryLoadLyricToUpdate(lyricName);

                ActivityUtils.setIsNewLyric(false, getActivity());
                ActivityUtils.setLyricFileNameParameter(lyricName, getActivity().getIntent());
                savedSuccessfully = true;

                mListener.onSaveItem(mLyric);
                mIsTempFileUsed = false;
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return savedSuccessfully;
    }

    private void addLyric(String lyricName, String lyricNumber) throws Exception {
        AddLyricCommand cmd = new AddLyricCommand(lyricName, getTextContent(), lyricNumber);
        mAppService.addLyric(cmd);
    }

    private void updateLyric(String lyricName, String lyricNumber) throws Exception {
        String oldName = mLyric.getName();
        if (mIsTempFileUsed) {
            oldName = ActivityUtils.getLyricFileNameParameter(getActivity().getIntent());
        }
        UpdateLyricCommand cmd = new UpdateLyricCommand(lyricName, getTextContent(),
                lyricNumber, oldName);
        mAppService.updateLyric(cmd);
    }

    private boolean validateRequiredFields(String lyricName, String lyricNumber, String lyricContent) {
        boolean valid = true;
        if (lyricName.trim().equals("")) {
            setErrorFileNameRequired();
            valid = false;
        }
        if (lyricNumber.trim().equals("")) {
            setErrorSongNumberRequired();
            valid = false;
        }
        if (lyricContent.trim().equals("")) {
            setErrorLyricContentRequired();
            valid = false;
        }
        return valid;
    }

    private void setSaveButtonOnClickListener(View v) {
        ImageButton btnSaveLyric = v.findViewById(R.id.btnSave);
        btnSaveLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveLyricFile()) {
                    String message = getResources().getString(R.string.file_saved);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maintain_lyric, container, false);

        EditText etLyricContent = v.findViewById(R.id.etTextFile);
        EditText etLyricName = v.findViewById(R.id.etFileName);
        EditText etSongNumber = v.findViewById(R.id.etSongNumber);
        etSongNumber.requestFocus();

        tryLoadLyric(etLyricContent, etLyricName, etSongNumber);
        setSaveButtonOnClickListener(v);

        return v;
    }

    private void tryLoadLyric(EditText etLyricContent, EditText etLyricName, EditText etSongNumber) {
        mLyric = tryLoadLyricFromTempFile();
        if (mLyric == null) {
            String lyricName = ActivityUtils.getLyricFileNameParameter(getActivity().getIntent());
            mLyric = tryLoadLyricToUpdate(lyricName);
        }
        setTextFields(mLyric, etLyricContent, etLyricName, etSongNumber);
    }

    private Lyric tryLoadLyricFromTempFile() {
        try {
            Lyric lyric = mAppService.loadLyricWithConfiguration(mAppService.getTempLyricName(), true);
            if (lyric != null) {
                ArrayList<String> removeTemp = new ArrayList<>();
                removeTemp.add(lyric.getName());
                mAppService.removeLyrics(removeTemp);
                lyric.removeNameSuffix(mAppService.getTempLyricName());
                mOperation = ActivityUtils.isNewLyric(getActivity()) ? Operation.NEW_LYRIC : Operation.EDIT_LYRIC;
                mIsTempFileUsed = true;
                return lyric;
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private void setTextFields(Lyric lyric, EditText etLyricContent, EditText etLyricName, EditText etSongNumber) {
        if (lyric != null) {
            etLyricContent.setText(lyric.getContent());
            etLyricName.setText(lyric.getName());
            int songNumber = lyric.getConfiguration().getSongNumber();
            etSongNumber.setText(String.valueOf(songNumber == Integer.MIN_VALUE ? "" : songNumber));
        }
    }

    private Lyric tryLoadLyricToUpdate(String lyricName) {
        if (lyricName != null) {
            mOperation = Operation.EDIT_LYRIC;
            mIsTempFileUsed = false;
            try {
                return mAppService.loadLyricWithConfiguration(lyricName, false);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        AndroidInjection.inject(this);
        super.onAttach(context);
        attachListener(context);
    }

    private void attachListener(Context context) {
        if (context instanceof OnSaveItemListener) {
            mListener = (OnSaveItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSaveItemListener");
        }
    }

    private void setErrorFileNameRequired() {
        if (getView() != null) {
            EditText etFileName = getView().findViewById(R.id.etFileName);
            String error = getResources().getString(R.string.file_name_required);

            etFileName.setError(error);
        }
    }

    private void setErrorSongNumberRequired() {
        if (getView() != null) {
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);
            String error = getResources().getString(R.string.song_number_required);

            etSongNumber.setError(error);
        }
    }

    private void setErrorLyricContentRequired() {
        if (getView() != null) {
            EditText etLyricContent = getView().findViewById(R.id.etTextFile);
            String error = getResources().getString(R.string.lyric_content_required);

            etLyricContent.setError(error);
        }
    }

    private String getTextContent() {
        if (getView() != null) {
            EditText etTextFile = getView().findViewById(R.id.etTextFile);
            return etTextFile.getText().toString();
        }
        return "";
    }

    private String getFileNameContent() {
        if (getView() != null) {
            EditText etFileName = getView().findViewById(R.id.etFileName);
            return etFileName.getText().toString();
        }
        return "";
    }

    private String getSongNumberContent() {
        if (getView() != null) {
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);
            return etSongNumber.getText().toString();
        }
        return "";
    }

    /**
     * This method is here only to support older versions (before API 23).
     *
     * @param activity Activity to be attached to this fragment
     */
    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
        attachListener(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateContent(String lyricName) {
        if (lyricName == null) return;

        mOperation = Operation.EDIT_LYRIC;
        ActivityUtils.setLyricFileNameParameter(lyricName, getActivity().getIntent());
        boolean userSelectedALyric = ActivityUtils.isClickedOnLyric(getActivity());

        if (getView() != null && (userSelectedALyric || !mIsTempFileUsed)) {
            EditText etLyricContent = getView().findViewById(R.id.etTextFile);
            EditText etLyricName = getView().findViewById(R.id.etFileName);
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);

            tryLoadLyric(etLyricContent, etLyricName, etSongNumber);
        }
    }

    public void newContent() {
        boolean wasNewLyricBefore = ActivityUtils.isNewLyric(getActivity());
        if (getView() != null) {
            ActivityUtils.setIsNewLyric(true, getActivity());
            EditText etLyricContent = getView().findViewById(R.id.etTextFile);
            EditText etLyricName = getView().findViewById(R.id.etFileName);
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);

            if (!wasNewLyricBefore) {
                etLyricContent.setText("");
                etLyricName.setText("");
                etSongNumber.setText("");
            }

            mOperation = Operation.NEW_LYRIC;
        }
    }

    public interface OnSaveItemListener {
        void onSaveItem(Lyric lyric);
    }

    private enum Operation {
        NEW_LYRIC,
        EDIT_LYRIC
    }
}