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
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, 
* SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.pool;

import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.CoroutinePool;

/**
 * Wrapper de compatibilidade para ThreadPool.java que delega para CoroutinePool (Kotlin).
 * 
 * Este arquivo mantém 100% de compatibilidade com o código existente enquanto
 * usa internamente o novo sistema CoroutinePool otimizado para Java 21.
 * 
 * TODAS as APIs são idênticas ao ThreadPool.java original:
 * - init()
 * - execute(Runnable)
 * - schedule(Runnable, long)
 * - scheduleAtFixedRate(Runnable, long, long)
 * - executePathfinding(Runnable)
 * - executeParallel(Runnable) - agora usa ForkJoinPool (bloqueante, igual original)
 * - getPathfindingQueueSize()
 * - getPathfindingActiveCount()
 * - shutdown()
 * 
 * MIGRAÇÃO:
 * - Nenhuma mudança necessária no código Java existente
 * - Todas as chamadas continuam funcionando
 * - Performance melhorada automaticamente com Virtual Threads e otimizações Java 21
 */
public final class ThreadPool
{
	/**
	 * Inicializa o pool de threads (delega para CoroutinePool.init()).
	 */
	public static void init()
	{
		CoroutinePool.init();
	}

	/**
	 * Executa uma tarefa instantânea (delega para CoroutinePool.execute()).
	 * @param r a tarefa a ser executada
	 */
	public static void execute(Runnable r)
	{
		CoroutinePool.execute(r);
	}

	/**
	 * Agenda uma tarefa para execução futura (delega para CoroutinePool.schedule()).
	 * @param r a tarefa a ser agendada
	 * @param delay o delay em milissegundos
	 * @return ScheduledFuture ou null se houve erro (compatível com implementação original)
	 */
	public static ScheduledFuture<?> schedule(Runnable r, long delay)
	{
		try
		{
			return CoroutinePool.schedule(r, delay);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
     * Executa uma tarefa de I/O (banco de dados, rede, leitura de arquivo).
     * Delega para o CoroutinePool.executeIO() que utiliza Virtual Threads.
     * Ideal para evitar bloqueio nas threads de jogo principais (Game/AI).
     * 
     * @param r a tarefa de I/O a ser executada
     */
    public static void executeIO(Runnable r)
    {
        CoroutinePool.executeIO(r);
    }
    
    /**
     * Agenda uma tarefa de I/O para execução futura.
     * Como o schedule original usa threads físicas, delegamos o agendamento
     * e forçamos a execução real da tarefa dentro de uma Virtual Thread de I/O.
     * 
     * @param r a tarefa de I/O a ser agendada
     * @param delay o delay em milissegundos
     * @return ScheduledFuture ou null se houve erro
     */
    public static ScheduledFuture<?> scheduleIO(Runnable r, long delay)
    {
        try
        {
            return CoroutinePool.schedule(() -> executeIO(r), delay);
        }
        catch (Exception e)
        {
            return null;
        }
    }

	/**
	 * Agenda uma tarefa para execução periódica (delega para CoroutinePool.scheduleAtFixedRate()).
	 * @param r a tarefa a ser agendada
	 * @param delay o delay inicial em milissegundos
	 * @param period o período em milissegundos
	 * @return ScheduledFuture ou null se houve erro (compatível com implementação original)
	 */
	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay, long period)
	{
		try
		{
			return CoroutinePool.scheduleAtFixedRate(r, delay, period);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Executa tarefa de pathfinding no pool dedicado (delega para CoroutinePool.executePathfinding()).
	 * @param task a tarefa de pathfinding a ser executada
	 */
	public static void executePathfinding(Runnable task)
	{
		CoroutinePool.executePathfinding(task);
	}

	/**
	 * Executa tarefa em paralelo usando ForkJoinPool (bloqueante, igual implementação original).
	 * @param task a tarefa a ser executada em paralelo
	 */
	public static void executeParallel(Runnable task)
	{
		CoroutinePool.executeParallelBlocking(task);
	}

	/**
	 * Retorna o número de tarefas de pathfinding aguardando na fila.
	 * @return o tamanho da fila de pathfinding
	 */
	public static int getPathfindingQueueSize()
	{
		return CoroutinePool.getPathfindingQueueSize();
	}

	/**
	 * Retorna o número de threads ativas de pathfinding.
	 * @return o número de threads ativas
	 */
	public static int getPathfindingActiveCount()
	{
		return CoroutinePool.getPathfindingActiveCount();
	}

	/**
	 * Desliga todos os pools (delega para CoroutinePool.shutdown()).
	 */
	public static void shutdown()
	{
		CoroutinePool.shutdown();
	}
}
