package ca.ualberta.taskchecker;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class TaskListFragment extends ListFragment {
	
	private static final String TAG = "TaskListFragment";
	private ArrayList<Task> tasks;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.tasks_title);
		tasks = TaskHolder.get(getActivity()).getTasks();


		TaskAdapter adapter = new TaskAdapter(tasks);

		setListAdapter(adapter);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
	View v = super.onCreateView(inflater, parent, savedInstanceState);
	
	ListView listView = (ListView)v.findViewById(android.R.id.list);
	//registerForContextMenu(listView);
	listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	
	listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
		
		public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		}
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.task_list_item_context, menu);
			return true;
		}
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
				case R.id.menu_item_delete_task:
					TaskAdapter adapter = (TaskAdapter)getListAdapter();
					TaskHolder taskHolder = TaskHolder.get(getActivity());
					for (int i = adapter.getCount() - 1; i >= 0; i--) {
						if (getListView().isItemChecked(i)) {
							taskHolder.deleteTask(adapter.getItem(i));
						}
					}
					mode.finish();
					adapter.notifyDataSetChanged();
					return true;
				default:
					return false;
			}
		}
		public void onDestroyActionMode(ActionMode mode) {
		}
	});
	
	return v;

	}
	
	@Override
	public void onResume() {
		super.onResume();
		((TaskAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	super.onCreateOptionsMenu(menu, inflater);
	inflater.inflate(R.menu.fragment_task_list, menu);
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_task:
				Task crime = new Task();
				TaskHolder.get(getActivity()).addTask(crime);
				Intent i = new Intent(getActivity(), TaskActivity.class);
				i.putExtra(TaskFragment.EXTRA_TASK_ID, crime.getId());
				startActivityForResult(i, 0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//Task t = ((TaskAdapter)getListAdapter()).getItem(position);
		//t.setComplete(!t.isComplete());
		//((TaskAdapter)getListAdapter()).notifyDataSetChanged();
		
		Task t = ((TaskAdapter)getListAdapter()).getItem(position);
		
		Intent i = new Intent(getActivity(), TaskActivity.class);
		i.putExtra(TaskFragment.EXTRA_TASK_ID, t.getId());
		startActivity(i);
		
	}
	private class TaskAdapter extends ArrayAdapter<Task> {
		public TaskAdapter(ArrayList<Task> tasks) {
			super(getActivity(), 0, tasks);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_task, null);
			}
			
			Task t = getItem(position);
			
			TextView titleTextView = (TextView)convertView.findViewById(R.id.task_list_item_titleTextView);
			titleTextView.setText(t.getTitle());
			
			CheckBox completeCheckBox = (CheckBox)convertView.findViewById(R.id.task_list_item_completeCheckBox);
			completeCheckBox.setChecked(t.isComplete());
			
			return convertView;
			
			
			
		}
	}
}