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
package ext.mods.fakeplayer.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.data.xml.PlayerLevelData;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.enums.actors.Sex;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.container.player.Appearance;
import ext.mods.gameserver.model.actor.template.PlayerTemplate;
import ext.mods.gameserver.model.records.PlayerLevel;

import ext.mods.fakeplayer.EquipesManager;
import ext.mods.fakeplayer.FakePlayer;
import ext.mods.fakeplayer.FakePlayerNames;
import ext.mods.fakeplayer.ai.FakePlayerAI;
import ext.mods.fakeplayer.ai.FallbackAI;
import ext.mods.fakeplayer.thirdclasses.AdventurerAI;
import ext.mods.fakeplayer.thirdclasses.ArchmageAI;
import ext.mods.fakeplayer.thirdclasses.CardinalAI;
import ext.mods.fakeplayer.thirdclasses.DominatorAI;
import ext.mods.fakeplayer.thirdclasses.DreadnoughtAI;
import ext.mods.fakeplayer.thirdclasses.DuelistAI;
import ext.mods.fakeplayer.thirdclasses.GhostHunterAI;
import ext.mods.fakeplayer.thirdclasses.GhostSentinelAI;
import ext.mods.fakeplayer.thirdclasses.GrandKhavatariAI;
import ext.mods.fakeplayer.thirdclasses.MaestroAI;
import ext.mods.fakeplayer.thirdclasses.MoonlightSentinelAI;
import ext.mods.fakeplayer.thirdclasses.MysticMuseAI;
import ext.mods.fakeplayer.thirdclasses.SaggitariusAI;
import ext.mods.fakeplayer.thirdclasses.SoultakerAI;
import ext.mods.fakeplayer.thirdclasses.StormScreamerAI;
import ext.mods.fakeplayer.thirdclasses.TitanAI;
import ext.mods.fakeplayer.thirdclasses.WindRiderAI;

public class FakePlayerHelpers
{
	public static FakePlayer createRandomFakePlayer()
	{
		ClassId classId = getThirdClasses().get(Rnd.get(0, getThirdClasses().size() - 1));
		final PlayerTemplate template = PlayerData.getInstance().getTemplate(classId);
		Appearance app = getRandomAppearance(template.getRace());
		String playerName = FakePlayerNames.getUniqueName(app.getSex());
		
		if (playerName == null)
			return null;
		
		int objectId = IdFactory.getInstance().getNextId();
		String accountName = "AutoPilot";
		
		FakePlayer player = new FakePlayer(objectId, template, accountName, app);
		player.setName(playerName);
		
		player.setAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
		player.setBaseClass(player.getClassId());
		setLevel(player, 81);
		player.rewardSkills();
		EquipesManager.applyEquipment(player);
		player.heal();
		
		return player;
	}
	
	public static Appearance getRandomAppearance(ClassRace race)
	{
		
		Sex randomSex = Rnd.get(1, 2) == 1 ? Sex.MALE : Sex.FEMALE;
		int hairStyle = Rnd.get(0, randomSex == Sex.MALE ? 4 : 6);
		int hairColor = Rnd.get(0, 3);
		int faceId = Rnd.get(0, 2);
		
		return new Appearance((byte) faceId, (byte) hairColor, (byte) hairStyle, randomSex);
	}
	
	public static void setLevel(FakePlayer player, int level)
	{
		
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(level);
		
		final long pXp = player.getStatus().getExp();
		final long tXp = pl.requiredExpToLevelUp();
		
		if (pXp > tXp)
			player.removeExpAndSp(pXp - tXp, 0);
		else if (pXp < tXp)
			player.addExpAndSp(tXp - pXp, 0);
		
	}
	
	public static List<ClassId> getThirdClasses()
	{
		List<ClassId> classes = new ArrayList<>();
		
		classes.add(ClassId.SAGGITARIUS);
		classes.add(ClassId.ARCHMAGE);
		classes.add(ClassId.SOULTAKER);
		classes.add(ClassId.MYSTIC_MUSE);
		classes.add(ClassId.STORM_SCREAMER);
		classes.add(ClassId.MOONLIGHT_SENTINEL);
		classes.add(ClassId.GHOST_SENTINEL);
		classes.add(ClassId.ADVENTURER);
		classes.add(ClassId.WIND_RIDER);
		classes.add(ClassId.DOMINATOR);
		classes.add(ClassId.TITAN);
		classes.add(ClassId.CARDINAL);
		classes.add(ClassId.DUELIST);
		classes.add(ClassId.GRAND_KHAVATARI);
		classes.add(ClassId.DREADNOUGHT);
		classes.add(ClassId.MAESTRO);
		
		return classes;
	}
	
	public static Map<ClassId, Class<? extends FakePlayerAI>> getAllAIs()
	{
		Map<ClassId, Class<? extends FakePlayerAI>> ais = new HashMap<>();
		ais.put(ClassId.CARDINAL, CardinalAI.class);
		ais.put(ClassId.STORM_SCREAMER, StormScreamerAI.class);
		ais.put(ClassId.MYSTIC_MUSE, MysticMuseAI.class);
		ais.put(ClassId.ARCHMAGE, ArchmageAI.class);
		ais.put(ClassId.SOULTAKER, SoultakerAI.class);
		ais.put(ClassId.SAGGITARIUS, SaggitariusAI.class);
		ais.put(ClassId.MOONLIGHT_SENTINEL, MoonlightSentinelAI.class);
		ais.put(ClassId.GHOST_SENTINEL, GhostSentinelAI.class);
		ais.put(ClassId.ADVENTURER, AdventurerAI.class);
		ais.put(ClassId.WIND_RIDER, WindRiderAI.class);
		ais.put(ClassId.GHOST_HUNTER, GhostHunterAI.class);
		ais.put(ClassId.DOMINATOR, DominatorAI.class);
		ais.put(ClassId.TITAN, TitanAI.class);
		ais.put(ClassId.DUELIST, DuelistAI.class);
		ais.put(ClassId.GRAND_KHAVATARI, GrandKhavatariAI.class);
		ais.put(ClassId.DREADNOUGHT, DreadnoughtAI.class);
		ais.put(ClassId.MAESTRO, MaestroAI.class);
		return ais;
	}
	
	public static Class<? extends FakePlayerAI> getAIbyClassId(ClassId classId)
	{
		Class<? extends FakePlayerAI> ai = getAllAIs().get(classId);
		if (ai == null)
			return FallbackAI.class;
		
		return ai;
	}
	
	public static Class<? extends Creature> getTargetClass()
	{
		return Creature.class;
	}
	
	public static int getTargetRange()
	{
		return 5000;
	}
	
	public static int[][] getFighterBuffs()
	{
		return new int[][]
		{
			{
				1204,
				2
			},
			{
				1040,
				3
			},
			{
				1035,
				4
			},
			{
				1045,
				6
			},
			{
				1068,
				3
			},
			{
				1062,
				2
			},
			{
				1086,
				2
			},
			{
				1077,
				3
			},
			{
				1388,
				3
			},
			{
				1036,
				2
			},
			{
				274,
				1
			},
			{
				273,
				1
			},
			{
				268,
				1
			},
			{
				271,
				1
			},
			{
				267,
				1
			},
			{
				349,
				1
			},
			{
				264,
				1
			},
			{
				269,
				1
			},
			{
				364,
				1
			},
			{
				1363,
				1
			},
			{
				4699,
				5
			}
		};
	}
	
	public static int[][] getMageBuffs()
	{
		return new int[][]
		{
			{
				1204,
				2
			},
			{
				1040,
				3
			},
			{
				1035,
				4
			},
			{
				4351,
				6
			},
			{
				1036,
				2
			},
			{
				1045,
				6
			},
			{
				1303,
				2
			},
			{
				1085,
				3
			},
			{
				1062,
				2
			},
			{
				1059,
				3
			},
			{
				1389,
				3
			},
			{
				273,
				1
			},
			{
				276,
				1
			},
			{
				365,
				1
			},
			{
				264,
				1
			},
			{
				268,
				1
			},
			{
				267,
				1
			},
			{
				349,
				1
			},
			{
				1413,
				1
			},
			{
				4703,
				4
			}
		};
	}
	
}
