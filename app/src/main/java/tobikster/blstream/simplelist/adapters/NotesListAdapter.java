package tobikster.blstream.simplelist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tobikster.blstream.simplelist.R;
import tobikster.blstream.simplelist.model.Note;

/**
 * Created by tobikster on 2015-10-21.
 */
public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder> {

	private final LayoutInflater mLayoutInflater;
	private final List<Note> mNotes;
	private NoteViewHolder.OnInteractionListener mOnInteractionListener;
	private SparseBooleanArray mSelectedItems;

	public NotesListAdapter(Context context, List<Note> notes, NoteViewHolder.OnInteractionListener onInteractionListener) {
		mLayoutInflater = LayoutInflater.from(context);
		mNotes = new ArrayList<>(notes);
		mOnInteractionListener = onInteractionListener;
		mSelectedItems = new SparseBooleanArray();
	}

	@Override
	public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View itemView = mLayoutInflater.inflate(R.layout.notes_list_item, parent, false);
		return new NoteViewHolder(itemView, mOnInteractionListener);
	}

	@Override
	public void onBindViewHolder(NoteViewHolder holder, int position) {
		final Note note = mNotes.get(position);
		holder.bind(note);
	}

	@Override
	public int getItemCount() {
		return mNotes.size();
	}

	public void animateTo(List<Note> notes) {
		applyAndAnimateRemovals(notes);
		applyAndAnimateAdditions(notes);
		applyAndAnimateMovedItems(notes);
	}

	private void applyAndAnimateRemovals(List<Note> newNotes) {
		for(int i = mNotes.size() - 1; i >= 0; i--) {
			final Note note = mNotes.get(i);
			if(!newNotes.contains(note)) {
				removeItem(i);
			}
		}
	}

	private void applyAndAnimateAdditions(List<Note> newNotes) {
		for(int i = 0, count = newNotes.size(); i < count; i++) {
			final Note note = newNotes.get(i);
			if(!mNotes.contains(note)) {
				addItem(i, note);
			}
		}
	}

	private void applyAndAnimateMovedItems(List<Note> newNotes) {
		for(int toPosition = newNotes.size() - 1; toPosition >= 0; toPosition--) {
			final Note note = newNotes.get(toPosition);
			final int fromPosition = mNotes.indexOf(note);
			if(fromPosition >= 0 && fromPosition != toPosition) {
				moveItem(fromPosition, toPosition);
			}
		}
	}

	public Note removeItem(int position) {
		final Note note = mNotes.remove(position);
		notifyItemRemoved(position);
		return note;
	}

	public void addItem(int position, Note note) {
		mNotes.add(position, note);
		notifyItemInserted(position);
	}

	public void updateItems(Note note) {
		int index = mNotes.indexOf(note);
		if(index >= 0) {
			Note tmp = mNotes.get(index);
			tmp.setTitle(note.getTitle());
			tmp.setChecked(note.isChecked());
			notifyItemChanged(index);
		}
		else {
			addItem(mNotes.size(), note);
		}
	}

	public void moveItem(int fromPosition, int toPosition) {
		final Note note = mNotes.remove(fromPosition);
		mNotes.add(toPosition, note);
		notifyItemMoved(fromPosition, toPosition);
	}

	public Note getItem(int position) {
		return mNotes.get(position);
	}

	public void toggleSelection(int position) {
		if(mSelectedItems.get(position, false)) {
			mSelectedItems.delete(position);
		}
		else {
			mSelectedItems.put(position, true);
		}
		notifyItemChanged(position);
	}

	public void clearSelections() {
		mSelectedItems.clear();
		notifyDataSetChanged();
	}

	public int getSelectedItemsCount() {
		return mSelectedItems.size();
	}

	public List<Integer> getSelectedItems() {
		List<Integer> items = new ArrayList<>(mSelectedItems.size());
		for(int i = 0; i < mSelectedItems.size(); ++i) {
			items.add(mSelectedItems.keyAt(i));
		}
		return items;
	}

	public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

		private final TextView mNoteTitle;
		private final CheckBox mNoteCheckbox;
		private OnInteractionListener mOnInteractionListener;

		public NoteViewHolder(View itemView, OnInteractionListener listener) {
			super(itemView);

			mNoteTitle = (TextView)itemView.findViewById(R.id.note_title);
			mNoteCheckbox = (CheckBox)itemView.findViewById(R.id.checkBox);
			mOnInteractionListener = listener;
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
			mNoteCheckbox.setOnCheckedChangeListener(this);
		}

		public void bind(Note note) {
			mNoteTitle.setText(note.getTitle());
			mNoteCheckbox.setChecked(note.isChecked());
		}

		@Override
		public void onClick(View v) {
			if(mOnInteractionListener != null) {
				mOnInteractionListener.onItemClicked(v, getAdapterPosition());
			}
		}

		@Override
		public boolean onLongClick(View v) {
			boolean result = false;
			if(mOnInteractionListener != null) {
				result = mOnInteractionListener.onItemLongClicked(v, getAdapterPosition());
			}
			return result;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(mOnInteractionListener != null) {
				mOnInteractionListener.onItemCheckboxChanged(buttonView, getAdapterPosition(), isChecked);
			}
		}

		public interface OnInteractionListener {
			void onItemClicked(View view, int position);
			boolean onItemLongClicked(View view, int position);
			void onItemCheckboxChanged(CompoundButton v, int position, boolean state);
		}
	}
}
