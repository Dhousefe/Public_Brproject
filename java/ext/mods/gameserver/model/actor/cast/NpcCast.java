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
package ext.mods.gameserver.model.actor.cast;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.scripting.Quest;

/**
 * Esta classe agrupa todos os dados de lançamento (cast) relacionados a um {@link Npc}.
 */
public class NpcCast extends CreatureCast<Npc>
{
    public NpcCast(Npc actor)
    {
        super(actor);
    }
    
    @Override
    protected final void onMagicHitTimer()
    {
        if (!isCastingNow())
            return;
        
        final double mpConsume = _actor.getStatus().getMpConsume(_skill);
        if (mpConsume > 0)
        {
            if (mpConsume > _actor.getStatus().getMp())
            {
                stop();
                return;
            }
            
            _actor.getStatus().reduceMp(mpConsume);
        }
        
        final double hpConsume = _skill.getHpConsume();
        if (hpConsume > 0)
        {
            if (hpConsume > _actor.getStatus().getHp())
            {
                stop();
                return;
            }
            
            _actor.getStatus().reduceHp(hpConsume, _actor, true);
        }
        
        callSkill(_skill, _targets, _item);
        
        _castTask = ThreadPool.schedule(this::onMagicFinalizer, _coolTime + 250);
    }
    
    @Override
    protected void notifyCastFinishToAI(boolean isInterrupted)
    {
        _actor.getAI().clearCurrentDesire();
        
        final Creature target = (_targets != null && _targets.length > 0) ? _targets[0] : _target;
        
        for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.USE_SKILL_FINISHED))
            quest.onUseSkillFinished(_actor, target, _skill, !isInterrupted);
        
        if (!isInterrupted && target != null)
        {
            _actor.getMove().repositionAfterAttack(target);
        }
        
        if (!isInterrupted)
            _actor.getAI().notifyEvent(AiEventType.FINISHED_CASTING, null, null);
    }
}