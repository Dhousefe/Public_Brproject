/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.ScheduledQuest;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and stores {@link Quest}s - being regular quests, AI scripts or scheduled scripts.
 */
public final class ScriptData implements IXmlReader, Runnable
{
	public static final int PERIOD = 5 * 60 * 1000;
	
	private final List<Quest> _quests = new ArrayList<>();
	private final List<ScheduledQuest> _scheduled = new LinkedList<>();
	
	private ScheduledFuture<?> _scheduledTask;
	
	public ScriptData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/scripts.xml");
		LOGGER.info("Loaded {} regular scripts and {} scheduled scripts.", _quests.size(), _scheduled.size());
		
		_scheduledTask = ThreadPool.scheduleAtFixedRate(this, 0, PERIOD);
	}
	
	@Override
	public void parseDocument(Document doc, Path p)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "script", scriptNode ->
		{
			final NamedNodeMap params = scriptNode.getAttributes();
			final String path = parseString(params, "path");
			if (path == null)
			{
				LOGGER.warn("One of the script path isn't defined.");
				return;
			}
			
			try
			{
				Quest instance = (Quest) Class.forName("ext.mods.gameserver.scripting." + path).getDeclaredConstructor().newInstance();
				
				_quests.add(instance);
				
				if (instance instanceof DefaultNpc)
					instance.feedEventHandlers();
				
				if (instance instanceof ScheduledQuest sq)
				{
					final String type = parseString(params, "schedule");
					if (type == null)
						return;
					
					final String start = parseString(params, "start");
					if (start == null)
					{
						LOGGER.warn("Missing 'start' parameter for scheduled script '{}'.", path);
						return;
					}
					
					final String end = parseString(params, "end");
					
					if (sq.setSchedule(type, start, end))
						_scheduled.add(sq);
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Script '{}' is missing.", e, path);
			}
		}));
	}
	
	@Override
	public void run()
	{
		final long next = System.currentTimeMillis() + PERIOD;
		
		for (ScheduledQuest script : _scheduled)
		{
			final long eta = next - script.getTimeNext();
			if (eta > 0)
				script.setTask(ThreadPool.schedule(new Scheduler(script), PERIOD - eta));
		}
	}
	
	public void reload()
	{
		if (_scheduledTask != null)
		{
			_scheduledTask.cancel(false);
			_scheduledTask = null;
		}
		
		_quests.clear();
		
		for (ScheduledQuest script : _scheduled)
			script.cleanTask();
		
		_scheduled.clear();
		
		load();
	}
	
	/**
	 * Notifies all {@link ScheduledQuest} to stop.<br>
	 * <br>
	 * Note: Can be used to store script values and variables.
	 */
	public final void stopAllScripts()
	{
		for (ScheduledQuest sq : _scheduled)
			sq.stop();
	}
	
	/**
	 * Returns the {@link Quest} by given quest name.
	 * @param questName : The name of the quest.
	 * @return Quest : Quest to be returned, null if quest does not exist.
	 */
	public final Quest getQuest(String questName)
	{
		return _quests.stream().filter(q -> q.getName().equalsIgnoreCase(questName)).findFirst().orElse(null);
	}
	
	/**
	 * Returns the {@link Quest} by given quest id.
	 * @param questId : The id of the quest.
	 * @return Quest : Quest to be returned, null if quest does not exist.
	 */
	public final Quest getQuest(int questId)
	{
		return _quests.stream().filter(q -> q.getQuestId() == questId).findFirst().orElse(null);
	}
	
	/**
	 * @return the {@link List} of {@link Quest}s.
	 */
	public final List<Quest> getQuests()
	{
		return _quests;
	}
	
	private final class Scheduler implements Runnable
	{
		private final ScheduledQuest _script;
		
		protected Scheduler(ScheduledQuest script)
		{
			_script = script;
		}
		
		@Override
		public void run()
		{
			_script.notifyAndSchedule();
			
			final long eta = System.currentTimeMillis() + PERIOD - _script.getTimeNext();
			if (eta > 0)
				_script.setTask(ThreadPool.schedule(this, PERIOD - eta));
		}
	}
	
	public static ScriptData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ScriptData INSTANCE = new ScriptData();
	}
}