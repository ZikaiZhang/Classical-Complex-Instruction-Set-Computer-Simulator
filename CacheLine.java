package computerArchitecture.simulatorproject.teamfour.firstphase;

public class CacheLine {
	
	short valid;
	
	short tag;
	
	long index;
	
	short[] lineElement;
	
	public CacheLine()
	{
		valid = 0;
		
		tag = 0;
		
		index = 0;
		
		lineElement = new short[SimulatorIdentifiers.CACHE_OFFSETBITS ^ 2];
		
		for(int i = 0; i < (SimulatorIdentifiers.CACHE_OFFSETBITS ^ 2); i++)
		{
			lineElement[i] = 0;
		}
	}
	
	
}
