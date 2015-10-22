package tobikster.blstream.simplelist.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tobikster.blstream.simplelist.R;
import tobikster.blstream.simplelist.model.Note;

/**
 * Created by tobikster on 2015-10-22.
 */
public class NotesListAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater mInflater;
	private List<Note> mNotes;
	private List<Note> mFilteredNotes;
	private String mCurrentFilterQuery;
	private OnItemInteractionListener mOnItemInteractionListener;

	public NotesListAdapter(Context context, List<Note> notes, OnItemInteractionListener onItemInteractionListener) {
		mInflater = LayoutInflater.from(context);
		mNotes = new ArrayList<>(notes);
		mFilteredNotes = new ArrayList<>(mNotes);
		mCurrentFilterQuery = "";
		mOnItemInteractionListener = onItemInteractionListener;
	}

	@Override
	public int getCount() {
		return mFilteredNotes.size();
	}

	@Override
	public Note getItem(int position) {
		return mFilteredNotes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Note note = getItem(position);
		ViewHolder viewHolder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.notes_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.noteTitle = (TextView)convertView.findViewById(R.id.note_title);
			viewHolder.noteCheckbox = (CheckBox)convertView.findViewById(R.id.note_check_box);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		viewHolder.noteTitle.setText(note.getTitle());
		viewHolder.noteCheckbox.setChecked(note.isChecked());
		viewHolder.noteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(mOnItemInteractionListener != null) {
					mOnItemInteractionListener.onItemCheckboxStateChanged(position, isChecked);
				}
			}
		});

		return convertView;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				mCurrentFilterQuery = constraint.toString().toLowerCase();

				List<Note> filteredList = new ArrayList<>();
				for(Note note : mNotes) {
					if(note.getTitle().toLowerCase().contains(mCurrentFilterQuery)) {
						filteredList.add(note);
					}
				}
				results.count = filteredList.size();
				results.values = filteredList;
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				//noinspection unchecked
				mFilteredNotes = (List<Note>)results.values;
				notifyDataSetChanged();
			}
		};
	}

	public void addNote(Note note) {
		mNotes.add(note);
		getFilter().filter(mCurrentFilterQuery);
	}

	public void modifyNote(Note note) {
		int index = mNotes.indexOf(note);
		Note oldNote = mNotes.get(index);
		oldNote.setTitle(note.getTitle());
		oldNote.setChecked(note.isChecked());
		getFilter().filter(mCurrentFilterQuery);
	}

	public Note removeNote(int position) {
		Note note = getItem(position);
		mNotes.remove(position);
		getFilter().filter(mCurrentFilterQuery);
		return note;
	}

	private static class ViewHolder {
		public TextView noteTitle;
		public CheckBox noteCheckbox;
	}

	public interface OnItemInteractionListener {
		void onItemCheckboxStateChanged(int position, boolean state);
	}
}
