package com.kunzisoft.remembirthday.activity;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kunzisoft.remembirthday.element.Contact;
import com.kunzisoft.remembirthday.element.DateUnknownYear;
import com.kunzisoft.remembirthday.task.ActionBirthdayInDatabaseTask;
import com.kunzisoft.remembirthday.task.UpdateBirthdayToContactTask;

/**
 * Abstract class to encapsulate the management of the birthday dialog.
 * @author joker on 06/07/17.
 */
public class AbstractBuddyActivity extends AppCompatActivity
        implements ActionBirthdayInDatabaseTask.CallbackActionBirthday, BirthdayDialogOpen {

    private static final String TAG_SELECT_DIALOG = "TAG_SELECT_DIALOG";

    protected Contact contactSelected;
    protected BirthdayDialogFragment dialogSelection;

    private OnClickDialogListener onClickDialogListener =
            new OnClickDialogListener(contactSelected);

    /**
     * Initialize Dialog for selection of date, must be called only if "contactSelected" is initialized
     */
    protected void initDialogSelection() {
        try {
            // Initialize dialog for birthday selection
            dialogSelection = (BirthdayDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_SELECT_DIALOG);
            if (dialogSelection == null)
                dialogSelection = new BirthdayDialogFragment();

            dialogSelection.setDefaultBirthday(contactSelected.getBirthday());
            dialogSelection.setOnClickListener(onClickDialogListener);
        } catch(NullPointerException e) {
            Log.e(this.getClass().getSimpleName(), "'contactSelected' must be initialized");
        }
    }

    public void setContactSelected(Contact contactSelected) {
        this.contactSelected = contactSelected;
        this.onClickDialogListener.setContact(contactSelected);
        this.dialogSelection.setOnClickListener(onClickDialogListener);
    }

    @Override
    public void openDialogSelection(long rawContactId) {
        try {
            Log.e("OK", contactSelected.toString());
            dialogSelection.show(getSupportFragmentManager(), TAG_SELECT_DIALOG);
        } catch(NullPointerException e) {
            Log.e(this.getClass().getSimpleName(), "'dialogSelection' must be initialized with 'initDialogSelection'");
        }
    }

    @Override
    public void afterActionBirthdayInDatabase(ActionBirthdayInDatabaseTask.CallbackActionBirthday.Action action, Exception exception) {
        CallbackAction.showMessage(this, action, exception);
    }

    private class OnClickDialogListener implements BirthdayDialogFragment.OnClickBirthdayListener {

        private Contact contact;

        public OnClickDialogListener(Contact contact) {
            this.contact = contact;
        }

        @Override
        public void onClickPositiveButton(DateUnknownYear dateUnknownYear) {
            // Update current birthday in database
            UpdateBirthdayToContactTask updateBirthdayToContactTask =
                    new UpdateBirthdayToContactTask(
                            AbstractBuddyActivity.this,
                            contact.getDataAnniversaryId(),
                            contact.getBirthday(),
                            dateUnknownYear);
            updateBirthdayToContactTask.setCallbackActionBirthday(AbstractBuddyActivity.this);
            updateBirthdayToContactTask.execute();
        }

        @Override
        public void onClickNegativeButton(DateUnknownYear selectedDate) {
        }

        public void setContact(Contact contact) {
            this.contact = contact;
        }
    }
}