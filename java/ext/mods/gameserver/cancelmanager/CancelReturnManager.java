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
package ext.mods.gameserver.cancelmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Gerencia o salvamento e restauração de buffs cancelados após um atraso configurável.
 * Utiliza recursos do Java 21 para melhor performance e thread-safety.
 * 
 * @author Dhousefe refatorado por Xpower
 */
public final class CancelReturnManager
{
	private static final CLogger LOGGER = new CLogger(CancelReturnManager.class.getName());
	
	/**
	 * Representa um buff salvo com suas informações essenciais.
	 */
	private static final class SavedBuff
	{
		private final int skillId;
		private final int level;
		private final int remainingSec;
		
		public SavedBuff(int skillId, int level, int remainingSec)
		{
			super();
			if (skillId <= 0) throw new IllegalArgumentException("Skill ID deve ser positivo");
			if (level <= 0) throw new IllegalArgumentException("Level deve ser positivo");
			if (remainingSec <= 0) throw new IllegalArgumentException("Tempo restante deve ser positivo");
			this.skillId = skillId;
			this.level = level;
			this.remainingSec = remainingSec;
		}
		
		public int skillId() { return skillId; }
		public int level() { return level; }
		public int remainingSec() { return remainingSec; }
	}
	
	/**
	 * Enum para modos de operação do CancelManager.
	 */
	public enum CancelMode
	{
		CANCEL_ONLY("Apenas skills de cancel"),
		CANCEL_AND_NEGATE("Cancel e negate"),
		ALL("Todos os tipos");
		
		private final String description;
		
		CancelMode(String description)
		{
			this.description = description;
		}
		
		public String getDescription()
		{
			return description;
		}
		
		public static CancelMode fromString(String value)
		{
			return switch (value.toUpperCase())
			{
				case "CANCEL_ONLY" -> CANCEL_ONLY;
				case "CANCEL_AND_NEGATE" -> CANCEL_AND_NEGATE;
				case "ALL" -> ALL;
				default -> CANCEL_ONLY;
			};
		}
	}
	
	private static final Map<Integer, Map<Integer, SavedBuff>> PENDING_BUFFS = new ConcurrentHashMap<>();
	
	private static final Map<Integer, CompletableFuture<Void>> PENDING_TASKS = new ConcurrentHashMap<>();
	
	private CancelReturnManager()
	{
		super();
		throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada");
	}
	
	/**
	 * Processa buffs cancelados por skills do tipo CANCEL.
	 * @param target Target que teve buffs cancelados
	 * @param cancelSkill Skill que cancelou os buffs
	 * @param cancelled Lista de efeitos cancelados
	 */
	public static void onCancel(Creature target, L2Skill cancelSkill, List<AbstractEffect> cancelled)
	{
		if (!isValidForProcessing(target, cancelled))
			return;
			
		if (!shouldProcessForMode(true))
			return;
			
		saveAndSchedule(target, cancelled);
	}
	
	/**
	 * Processa buffs cancelados por skills do tipo NEGATE.
	 * @param target Target que teve buffs cancelados
	 * @param negateSkill Skill que cancelou os buffs
	 * @param cancelled Lista de efeitos cancelados
	 */
	public static void onNegate(Creature target, L2Skill negateSkill, List<AbstractEffect> cancelled)
	{
		if (!isValidForProcessing(target, cancelled))
			return;
			
		if (!shouldProcessForMode(false))
			return;
			
		saveAndSchedule(target, cancelled);
	}
	
	/**
	 * Valida se o processamento deve ocorrer baseado nas configurações.
	 * @param target Target a ser processado
	 * @param cancelled Lista de efeitos cancelados
	 * @return true se deve processar, false caso contrário
	 */
	private static boolean isValidForProcessing(Creature target, List<AbstractEffect> cancelled)
	{
		if (!Config.CANCEL_RETURN_ENABLED)
			return false;
			
		if (target == null || cancelled == null || cancelled.isEmpty())
			return false;
			
		if (Config.CANCEL_RETURN_MASS_ONLY)
		{
			int minCount = Math.max(1, Config.CANCEL_RETURN_MASS_MIN_COUNT);
			if (cancelled.size() < minCount)
				return false;
		}
		
		if (Config.CANCEL_RETURN_SKIP_OLYMPIAD && target instanceof Player player && player.isInOlympiadMode())
			return false;
			
		return true;
	}
	
	/**
	 * Verifica se deve processar baseado no modo configurado.
	 * @param isCancel true se é um cancel, false se é negate
	 * @return true se deve processar, false caso contrário
	 */
	private static boolean shouldProcessForMode(boolean isCancel)
	{
		CancelMode mode = CancelMode.fromString(Config.CANCEL_RETURN_MODE);
		
		return switch (mode)
		{
			case CANCEL_ONLY -> isCancel;
			case CANCEL_AND_NEGATE, ALL -> true;
		};
	}
	
	/**
	 * Salva os buffs cancelados e agenda a restauração.
	 * @param target Target que teve buffs cancelados
	 * @param cancelled Lista de efeitos cancelados
	 */
	private static void saveAndSchedule(Creature target, List<AbstractEffect> cancelled)
	{
		final int objectId = target.getObjectId();
		
		cancelPreviousTask(objectId);
		
		Map<Integer, SavedBuff> buffsMap = PENDING_BUFFS.computeIfAbsent(objectId, k -> new ConcurrentHashMap<>());
		
		for (AbstractEffect effect : cancelled)
		{
			if (effect == null || effect.getSkill() == null)
				continue;
				
			L2Skill skill = effect.getSkill();
			
			if (!skill.canBeDispeled())
				continue;
				
			int period = effect.getPeriod();
			if (period <= 0)
				continue;
				
			int remaining = Math.max(1, period - effect.getTime());
			
			buffsMap.put(skill.getId(), new SavedBuff(skill.getId(), skill.getLevel(), remaining));
		}
		
		if (buffsMap.isEmpty())
		{
			PENDING_BUFFS.remove(objectId);
			return;
		}
		
		long delayMs = Math.max(0, Config.CANCEL_RETURN_TIME_MS);
		
		CompletableFuture<Void> task = CompletableFuture
			.runAsync(() -> {
				try
				{
					Thread.sleep(delayMs);
					restoreNow(target);
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			})
			.whenComplete((result, throwable) -> PENDING_TASKS.remove(objectId));
			
		PENDING_TASKS.put(objectId, task);
		
		if (Config.DEVELOPER)
		{
			LOGGER.info("CancelReturn: Salvou {} buffs para {} (restauração em {}ms)", 
				buffsMap.size(), target.getName(), delayMs);
		}
	}
	
	/**
	 * Cancela uma tarefa anterior se existir.
	 * @param objectId ID do objeto
	 */
	private static void cancelPreviousTask(int objectId)
	{
		CompletableFuture<Void> existingTask = PENDING_TASKS.remove(objectId);
		if (existingTask != null && !existingTask.isDone())
		{
			existingTask.cancel(true);
		}
	}
	
	/**
	 * Restaura os buffs salvos para o target.
	 * @param target Target para restaurar buffs
	 */
	private static void restoreNow(Creature target)
	{
		if (target == null || target.isDead())
		{
			cleanup(target);
			return;
		}
		
		final int objectId = target.getObjectId();
		final Map<Integer, SavedBuff> buffsMap = PENDING_BUFFS.remove(objectId);
		
		if (buffsMap == null || buffsMap.isEmpty())
			return;
			
		final List<SavedBuff> buffsToRestore = new ArrayList<>(buffsMap.values());
		boolean anyRestored = false;
		
		for (SavedBuff savedBuff : buffsToRestore)
		{
			try
			{
				if (target.getFirstEffect(savedBuff.skillId()) != null)
					continue;
					
				L2Skill skill = SkillTable.getInstance().getInfo(savedBuff.skillId(), savedBuff.level());
				if (skill == null)
					continue;
					
				List<AbstractEffect> newEffects = skill.getEffects(target, target);
				if (newEffects == null || newEffects.isEmpty())
					continue;
					
				for (AbstractEffect newEffect : newEffects)
				{
					if (newEffect == null)
						continue;
						
					newEffect.setPeriod(savedBuff.remainingSec());
					newEffect.setTime(0);
					newEffect.rescheduleEffect();
				}
				
				target.updateAbnormalEffect();
				anyRestored = true;
			}
			catch (Exception e)
			{
				LOGGER.warn("Erro ao restaurar buff {} para {}", savedBuff.skillId(), target.getName(), e);
			}
		}
		
		if (anyRestored && Config.CANCEL_RETURN_NOTIFY && target instanceof Player player)
		{
			player.sendMessage(Config.CANCEL_RETURN_MESSAGE);
		}
		
		if (Config.DEVELOPER)
		{
			LOGGER.info("CancelReturn: Restaurou {} buffs para {}", buffsToRestore.size(), target.getName());
		}
	}
	
	/**
	 * Limpa dados de um target específico.
	 * @param target Target para limpar dados
	 */
	public static void cleanup(Creature target)
	{
		if (target == null)
			return;
			
		final int objectId = target.getObjectId();
		
		cancelPreviousTask(objectId);
		
		PENDING_BUFFS.remove(objectId);
	}
	
	/**
	 * Limpa todos os dados (útil para shutdown).
	 */
	public static void cleanup()
	{
		PENDING_TASKS.values().forEach(task -> {
			if (!task.isDone())
				task.cancel(true);
		});
		
		PENDING_TASKS.clear();
		PENDING_BUFFS.clear();
		
		LOGGER.info("CancelReturn: Limpeza completa realizada");
	}
	
	/**
	 * Retorna estatísticas do sistema.
	 * @return String com estatísticas do sistema
	 */
	public static String getStats()
	{
		int totalPendingBuffs = PENDING_BUFFS.values().stream()
			.mapToInt(Map::size)
			.sum();
			
		return String.format("CancelReturn Stats: %d targets, %d buffs pendentes, %d tarefas ativas", 
			PENDING_BUFFS.size(), totalPendingBuffs, PENDING_TASKS.size());
	}
}
