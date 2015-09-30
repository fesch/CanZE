package lu.fisch.canze.dialogues;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by robertfisch on 30.09.2015.
 */
public class YesNoDialog extends DialogFragment
{
    public YesNoDialog()
    {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        String title = args.getString("title", "");
        String message = args.getString("message", "");
        String yes = args.getString("yes", "");
        String no = args.getString("no", "");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                    }
                })
                .create();
    }
}
