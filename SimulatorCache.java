package computerArchitecture.simulatorproject.teamfour.firstphase;

import java.util.concurrent.ThreadLocalRandom;

//it is a fully-associative, 16-line, 16 word per line, write-through, no-write-allocate cache
public class SimulatorCache 
{
	//prepare to set an array of cache lines
	public CacheLine[] cacheLine;
	
	//prepare a long type variable keeping track the order a particular cache line is update
	public long index;
	
	//constructor
	public SimulatorCache()
	{
		//set an array of cache lines (sixteen of them) and initialize all elements of cache lines to zero
		cacheLine = new CacheLine[SimulatorIdentifiers.CACHE_LINES];
		
		//initialized the variable mentioned previously to zero
		index = 0;
	}
	
	//method for checking if an address results in a cache hit or not
	public boolean cacheHit(short address)
	{
		//get the tag bits of an address by a right shift
		short tag = (short) (address >> SimulatorIdentifiers.CACHE_OFFSETBITS);
		
		//determine if it is a cache hit
		for(int i = 0; i < SimulatorIdentifiers.CACHE_LINES; i++)
		{
			if((cacheLine[i].valid == 1) && (cacheLine[i].tag == tag))
			{
				return true;
			}
		}
		return false;
	}
	
	//method for return the required word from cache
	public short readCache(short address)
	{
		//get tag bits of an address by a right shift
	    short tag = (short) (address >> SimulatorIdentifiers.CACHE_OFFSETBITS);
		
		//get offset bits of an address by a mask operation
		short offset = (short) (address | 0x000F);
		
		while(true)
		{
			//if it is a cache hit, return the word
			if(cacheHit(address) == true)
			{
				for(int i = 0; i < SimulatorIdentifiers.CACHE_LINES; i++)
				{
					if((cacheLine[i].valid == 1) && (cacheLine[i].tag == tag))
					{
						return cacheLine[i].lineElement[offset];
					}
				}
			}
			
			//if it is a cache miss, update the cache
			else
			{
				updateCache(address);
			}
		}		
	}
	
	//method for updating cache when there is a cache miss
	public void updateCache(short address)
	{
		//determine if the cache is full
		int counter = 0;
		
		for(int i = 0; i < SimulatorIdentifiers.CACHE_LINES; i++)
		{
			if(cacheLine[i].valid == 1)
			{
				counter++;
			}
		}
		
		//if the cache is not full
		if(counter < 16)
		{
			//set variable storing the index of the cache line to be updated
			int lineNo;
			
			//get the random line number to be updated(can be improved)
			do
			{
				lineNo = ThreadLocalRandom.current().nextInt(0, SimulatorIdentifiers.CACHE_LINES - 1 + 1);
			}
			while(cacheLine[lineNo].valid == 0);
			
			//update the content of a cache line
			cacheLine[lineNo].valid = 1;
			
			cacheLine[lineNo].tag = (short) (address >> SimulatorIdentifiers.CACHE_OFFSETBITS);
			
				//update the entire block of a cache line
			for(int i = (address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE; 
					i < (address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE + SimulatorIdentifiers.CACHE_WORDPERLINE; i++)
			{
				cacheLine[lineNo].lineElement[i - (address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE]
						= SimulatorControl.MemoryUnit.readMemory((short) ((address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE));					
			}
			
			//increase the index by 1
			index++;
			
				//update the index field of the cache line being updated
			cacheLine[lineNo].index = index;
		}
		
		//if the cache line is full
		else
		{
			//select the first updated cache line of the sixteen
			long temp = 0;
			
			//set variable storing the index of the cache line to be updated
			int lineNo = 0;
			
			//select the line with the smallest index value(first added in the cache)
			for(int i = 0; i < SimulatorIdentifiers.CACHE_LINES; i++)
			{
				if(temp < cacheLine[i].index)
				{
					temp = cacheLine[i].index;
					
					lineNo = i;
				}
			}
			
			//update the content of a cache line
			cacheLine[lineNo].valid = 1;
			
			cacheLine[lineNo].tag = (short) (address >> SimulatorIdentifiers.CACHE_OFFSETBITS);
			
				//update the entire block of a cache line
			for(int i = (address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE; 
					i < (address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE + SimulatorIdentifiers.CACHE_WORDPERLINE; i++)
			{
				cacheLine[lineNo].lineElement[i - (address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE]
						= SimulatorControl.MemoryUnit.readMemory((short) ((address / SimulatorIdentifiers.CACHE_WORDPERLINE) * SimulatorIdentifiers.CACHE_WORDPERLINE));					
			}
			
			//increase the index by 1
			index++;
			
				//update the index field of the cache line being updated
			cacheLine[lineNo].index = index;			
		}	
	}
	
	//method for writing content to the cache
	public void writeCache(short address, short value)
	{
		//if there is a cache hit
		if(cacheHit(address) == true)
		{
			//update the word within the cache
			
			//get the tag bits of an address by a right shift
			short tag = (short) (address >> SimulatorIdentifiers.CACHE_OFFSETBITS);
			
			//get the offset bits of an address by a mask operation
			short offset = (short) (address & 0x000F);
			
			//update the word in a particular cache line
			for(int i = 0; i < SimulatorIdentifiers.CACHE_LINES; i++)
			{
				if((cacheLine[i].valid == 1) && (cacheLine[i].tag == tag))
				{
					cacheLine[i].lineElement[offset] = value;
				}
			}
			
			//update the word within the memory
			SimulatorControl.MemoryUnit.writeMemory(address, value);
		}
			
		//if there is a cache miss
		else 
		{
			//update the word within the memory
			SimulatorControl.MemoryUnit.writeMemory(address, value);
		}
		
		//return
		return;
	}
	
	
}
