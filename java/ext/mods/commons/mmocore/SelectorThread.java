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
package ext.mods.commons.mmocore;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

public final class SelectorThread<T extends MMOClient<?>> extends Thread
{
	private static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
	
	private static final int HEADER_SIZE = 2;
	
	private final Selector _selector;
	
	private final IPacketHandler<T> _packetHandler;
	private final IMMOExecutor<T> _executor;
	private final IClientFactory<T> _clientFactory;
	private final IAcceptFilter _acceptFilter;
	
	private final int HELPER_BUFFER_SIZE;
	private final int HELPER_BUFFER_COUNT;
	private final int MAX_SEND_PER_PASS;
	private final int MAX_READ_PER_PASS;
	private final long SLEEP_TIME;
	private final boolean TCP_NODELAY;
	
	private final ByteBuffer DIRECT_WRITE_BUFFER;
	private final ByteBuffer WRITE_BUFFER;
	private final ByteBuffer READ_BUFFER;
	
	private final NioNetStringBuffer STRING_BUFFER;
	
	private final LinkedList<ByteBuffer> _bufferPool;
	
	private final NioNetStackList<MMOConnection<T>> _pendingClose;
	
	private boolean _shutdown;
	
	public SelectorThread(final SelectorConfig sc, final IMMOExecutor<T> executor, final IPacketHandler<T> packetHandler, final IClientFactory<T> clientFactory, final IAcceptFilter acceptFilter) throws IOException
	{
		super.setName("SelectorThread-" + super.threadId());
		
		HELPER_BUFFER_SIZE = sc.HELPER_BUFFER_SIZE;
		HELPER_BUFFER_COUNT = sc.HELPER_BUFFER_COUNT;
		MAX_SEND_PER_PASS = sc.MAX_SEND_PER_PASS;
		MAX_READ_PER_PASS = sc.MAX_READ_PER_PASS;
		
		SLEEP_TIME = sc.SLEEP_TIME;
		TCP_NODELAY = sc.TCP_NODELAY;
		
		DIRECT_WRITE_BUFFER = ByteBuffer.allocateDirect(sc.WRITE_BUFFER_SIZE).order(BYTE_ORDER);
		WRITE_BUFFER = ByteBuffer.wrap(new byte[sc.WRITE_BUFFER_SIZE]).order(BYTE_ORDER);
		READ_BUFFER = ByteBuffer.wrap(new byte[sc.READ_BUFFER_SIZE]).order(BYTE_ORDER);
		
		STRING_BUFFER = new NioNetStringBuffer(64 * 1024);
		
		_pendingClose = new NioNetStackList<>();
		_bufferPool = new LinkedList<>();
		
		for (int i = 0; i < HELPER_BUFFER_COUNT; i++)
			_bufferPool.addLast(ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER));
		
		_acceptFilter = acceptFilter;
		_packetHandler = packetHandler;
		_clientFactory = clientFactory;
		_executor = executor;
		_selector = Selector.open();
	}
	
	public final void openServerSocket(InetAddress address, int tcpPort) throws IOException
	{
		final ServerSocketChannel selectable = ServerSocketChannel.open();
		selectable.configureBlocking(false);
		selectable.socket().bind((address == null) ? new InetSocketAddress(tcpPort) : new InetSocketAddress(address, tcpPort));
		selectable.register(_selector, SelectionKey.OP_ACCEPT);
	}
	
	final ByteBuffer getPooledBuffer()
	{
		if (_bufferPool.isEmpty())
			return ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(BYTE_ORDER);
		
		return _bufferPool.removeFirst();
	}
	
	final void recycleBuffer(final ByteBuffer buf)
	{
		if (_bufferPool.size() < HELPER_BUFFER_COUNT)
		{
			buf.clear();
			_bufferPool.addLast(buf);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final void run()
	{
		int selectedKeysCount = 0;
		
		SelectionKey key;
		MMOConnection<T> con;
		
		Iterator<SelectionKey> selectedKeys;
		
		while (!_shutdown)
		{
			try
			{
				selectedKeysCount = _selector.selectNow();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if (selectedKeysCount > 0)
			{
				selectedKeys = _selector.selectedKeys().iterator();
				
				while (selectedKeys.hasNext())
				{
					key = selectedKeys.next();
					selectedKeys.remove();
					
					con = (MMOConnection<T>) key.attachment();
					
					switch (key.readyOps())
					{
						case SelectionKey.OP_CONNECT:
							finishConnection(key, con);
							break;
						
						case SelectionKey.OP_ACCEPT:
							acceptConnection(key, con);
							break;
						
						case SelectionKey.OP_READ:
							readPacket(key, con);
							break;
						
						case SelectionKey.OP_WRITE:
							writePacket(key, con);
							break;
						
						case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
							writePacket(key, con);
							if (key.isValid())
								readPacket(key, con);
							break;
					}
				}
			}
			
			synchronized (_pendingClose)
			{
				while (!_pendingClose.isEmpty())
				{
					try
					{
						con = _pendingClose.removeFirst();
						writeClosePacket(con);
						closeConnectionImpl(con.getSelectionKey(), con);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
			try
			{
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		closeSelectorThread();
	}
	
	private final void finishConnection(final SelectionKey key, final MMOConnection<T> con)
	{
		try
		{
			((SocketChannel) key.channel()).finishConnect();
		}
		catch (IOException e)
		{
			con.getClient().onForcedDisconnection();
			closeConnectionImpl(key, con);
		}
		
		if (key.isValid())
		{
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
		}
	}
	
	private final void acceptConnection(final SelectionKey key, MMOConnection<T> con)
	{
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc;
		
		try
		{
			while ((sc = ssc.accept()) != null)
			{
				final Socket socket = sc.socket();
				if (_acceptFilter == null || _acceptFilter.accept(socket))
				{
					sc.configureBlocking(false);
					
					final SelectionKey clientKey = sc.register(_selector, SelectionKey.OP_READ);
					
					con = new MMOConnection<>(this, sc.socket(), clientKey, TCP_NODELAY);
					con.setClient(_clientFactory.create(con));
					
					clientKey.attach(con);
				}
				else
					socket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private final void readPacket(final SelectionKey key, final MMOConnection<T> con)
	{
		if (!con.isClosed())
		{
			ByteBuffer buf;
			if ((buf = con.getReadBuffer()) == null)
				buf = READ_BUFFER;
			
			if (buf.position() == buf.limit())
			{
				closeConnectionImpl(key, con);
				return;
			}
			
			int result = -2;
			
			try
			{
				result = con.read(buf);
			}
			catch (IOException e)
			{
			}
			
			if (result > 0)
			{
				buf.flip();
				
				final T client = con.getClient();
				
				for (int i = 0; i < MAX_READ_PER_PASS; i++)
				{
					if (!tryReadPacket(key, client, buf, con))
						return;
				}
				
				if (buf.remaining() > 0)
				{
					if (buf == READ_BUFFER)
						allocateReadBuffer(con);
					else
						buf.compact();
				}
			}
			else
			{
				switch (result)
				{
					case 0, -1:
						closeConnectionImpl(key, con);
						break;
					
					case -2:
						con.getClient().onForcedDisconnection();
						closeConnectionImpl(key, con);
						break;
				}
			}
		}
	}
	
	private final boolean tryReadPacket(final SelectionKey key, final T client, final ByteBuffer buf, final MMOConnection<T> con)
	{
		switch (buf.remaining())
		{
			case 0:
				return false;
			
			case 1:
				key.interestOps(key.interestOps() | SelectionKey.OP_READ);
				
				if (buf == READ_BUFFER)
					allocateReadBuffer(con);
				else
					buf.compact();
				return false;
			
			default:
				final int dataPending = (buf.getShort() & 0xFFFF) - HEADER_SIZE;
				
				if (dataPending <= buf.remaining())
				{
					if (dataPending > 0)
					{
						final int pos = buf.position();
						parseClientPacket(pos, buf, dataPending, client);
						buf.position(pos + dataPending);
					}
					
					if (!buf.hasRemaining())
					{
						if (buf != READ_BUFFER)
						{
							con.setReadBuffer(null);
							recycleBuffer(buf);
						}
						else
							READ_BUFFER.clear();
						return false;
					}
					return true;
				}
				
				key.interestOps(key.interestOps() | SelectionKey.OP_READ);
				
				if (buf == READ_BUFFER)
				{
					buf.position(buf.position() - HEADER_SIZE);
					allocateReadBuffer(con);
				}
				else
				{
					buf.position(buf.position() - HEADER_SIZE);
					buf.compact();
				}
				return false;
		}
	}
	
	private final void allocateReadBuffer(final MMOConnection<T> con)
	{
		con.setReadBuffer(getPooledBuffer().put(READ_BUFFER));
		READ_BUFFER.clear();
	}
	
	private final void parseClientPacket(final int pos, final ByteBuffer buf, final int dataSize, final T client)
	{
		final boolean ret = client.decrypt(buf, dataSize);
		
		if (ret && buf.hasRemaining())
		{
			final int limit = buf.limit();
			buf.limit(pos + dataSize);
			final ReceivablePacket<T> cp = _packetHandler.handlePacket(buf, client);
			
			if (cp != null)
			{
				cp._buf = buf;
				cp._sbuf = STRING_BUFFER;
				cp._client = client;
				
				if (cp.read())
					_executor.execute(cp);
				
				cp._buf = null;
				cp._sbuf = null;
			}
			buf.limit(limit);
		}
	}
	
	private final void writeClosePacket(final MMOConnection<T> con)
	{
		SendablePacket<T> sp;
		synchronized (con.getSendQueue())
		{
			if (con.getSendQueue().isEmpty())
				return;
			
			while ((sp = con.getSendQueue().removeFirst()) != null)
			{
				WRITE_BUFFER.clear();
				
				putPacketIntoWriteBuffer(con.getClient(), sp);
				
				WRITE_BUFFER.flip();
				
				try
				{
					con.write(WRITE_BUFFER);
				}
				catch (IOException e)
				{
				}
			}
		}
	}
	
	protected final void writePacket(final SelectionKey key, final MMOConnection<T> con)
	{
		if (!prepareWriteBuffer(con))
		{
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
			return;
		}
		
		DIRECT_WRITE_BUFFER.flip();
		
		final int size = DIRECT_WRITE_BUFFER.remaining();
		
		int result = -1;
		
		try
		{
			result = con.write(DIRECT_WRITE_BUFFER);
		}
		catch (IOException e)
		{
		}
		
		if (result >= 0)
		{
			if (result == size)
			{
				synchronized (con.getSendQueue())
				{
					if (con.getSendQueue().isEmpty() && !con.hasPendingWriteBuffer())
					{
						key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
					}
				}
			}
			else
				con.createWriteBuffer(DIRECT_WRITE_BUFFER);
		}
		else
		{
			con.getClient().onForcedDisconnection();
			closeConnectionImpl(key, con);
		}
	}
	
	private final boolean prepareWriteBuffer(final MMOConnection<T> con)
	{
		boolean hasPending = false;
		DIRECT_WRITE_BUFFER.clear();
		
		if (con.hasPendingWriteBuffer())
		{
			con.movePendingWriteBufferTo(DIRECT_WRITE_BUFFER);
			hasPending = true;
		}
		
		if (DIRECT_WRITE_BUFFER.remaining() > 1 && !con.hasPendingWriteBuffer())
		{
			final NioNetStackList<SendablePacket<T>> sendQueue = con.getSendQueue();
			final T client = con.getClient();
			SendablePacket<T> sp;
			
			for (int i = 0; i < MAX_SEND_PER_PASS; i++)
			{
				synchronized (con.getSendQueue())
				{
					if (sendQueue.isEmpty())
						sp = null;
					else
						sp = sendQueue.removeFirst();
				}
				
				if (sp == null)
					break;
				
				hasPending = true;
				
				putPacketIntoWriteBuffer(client, sp);
				
				WRITE_BUFFER.flip();
				
				if (DIRECT_WRITE_BUFFER.remaining() >= WRITE_BUFFER.limit())
					DIRECT_WRITE_BUFFER.put(WRITE_BUFFER);
				else
				{
					con.createWriteBuffer(WRITE_BUFFER);
					break;
				}
			}
		}
		return hasPending;
	}
	
	private final void putPacketIntoWriteBuffer(final T client, final SendablePacket<T> sp)
	{
		WRITE_BUFFER.clear();
		
		final int headerPos = WRITE_BUFFER.position();
		final int dataPos = headerPos + HEADER_SIZE;
		WRITE_BUFFER.position(dataPos);
		
		sp._buf = WRITE_BUFFER;
		sp._client = client;
		sp.write();
		sp._buf = null;
		
		int dataSize = WRITE_BUFFER.position() - dataPos;
		WRITE_BUFFER.position(dataPos);
		client.encrypt(WRITE_BUFFER, dataSize);
		
		dataSize = WRITE_BUFFER.position() - dataPos;
		
		WRITE_BUFFER.position(headerPos);
		WRITE_BUFFER.putShort((short) (dataSize + HEADER_SIZE));
		WRITE_BUFFER.position(dataPos + dataSize);
	}
	
	final void closeConnection(final MMOConnection<T> con)
	{
		synchronized (_pendingClose)
		{
			_pendingClose.addLast(con);
		}
	}
	
	private final void closeConnectionImpl(final SelectionKey key, final MMOConnection<T> con)
	{
		try
		{
			con.getClient().onDisconnection();
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (IOException e)
			{
			}
			finally
			{
				con.releaseBuffers();
				key.attach(null);
				key.cancel();
			}
		}
	}
	
	public final void shutdown()
	{
		_shutdown = true;
	}
	
	protected void closeSelectorThread()
	{
		for (final SelectionKey key : _selector.keys())
		{
			try
			{
				key.channel().close();
			}
			catch (IOException e)
			{
			}
		}
		
		try
		{
			_selector.close();
		}
		catch (IOException e)
		{
		}
	}
}