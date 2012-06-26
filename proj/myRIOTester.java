import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;

import edu.washington.cs.cse490h.lib.Callback;
import edu.washington.cs.cse490h.lib.Utility;

/**
 * Class that tests the reliable, in-order message layer. Randomly sends
 * messages to other nodes and receives them in order from the RIO layer. This
 * continues until we send at least 100 messages to another node.
 */
public class myRIOTester extends RIONode {
	// The RIO layer is not correct in the presence of node failures.
	public static double getFailureRate() { return 0/100.0; }
	public static double getDropRate() { return 25/100.0; }
	public static double getDelayRate() { return 50/100.0; }

	private HashMap<Integer, Integer> receivedNums;
	private HashMap<Integer, Integer> nextNum;
	private Random randNumGen;
	private int numFinished;

	public static int NUM_NODES = 10;

	private boolean failed = false;
	
	@Override
	public String packetBytesToString(byte[] bytes) {
		RIOPacket packet = RIOPacket.unpack(bytes);
		if (packet == null) {
			return super.packetBytesToString(bytes);
		}
		return packet.toString();
	}	

	@Override
	public void start() {
		logOutput("Starting up...");

		// Generate a user-level synoptic event to indicate that the node started.
		logSynopticEvent("started");
		
		receivedNums = new HashMap<Integer, Integer>();
		nextNum = new HashMap<Integer, Integer>();
		randNumGen = new Random();
		numFinished = 0;
	}

	@Override
	public void onCommand(String command) {
                String[] commandArr = parseCommand(command);
                if (commandArr[2].equals("false")) {
                    System.out.println("Command Parse Error");
		    return;
                }
            
                RIOSend(Integer.parseInt(commandArr[1]),
                                         Protocol.RIOTEST_PKT,
                                         Utility.stringToByteArray(commandArr[0]));
                return;
                
	}
	
	@Override
	public void onRIOReceive(Integer from, int protocol, byte[] msg) {
		if (protocol != Protocol.RIOTEST_PKT) {
			logError("unknown protocol: " + protocol);
			return;
		}
		System.out.println("received " + (Utility.byteArrayToString(msg)));
/*		Integer receivedNum = receivedNums.get(from);
		if (receivedNum == null) {
			// If we've never seen this sender before
			if (i == 0) {
				correctReceive(from, 0);
			} else {
				failure(from, i);
			}
		} else {
			// If we've previously encountered the sender
			if (i == receivedNum + 1) {
				correctReceive(from, i);
			} else {
				failure(from, i);
			}
		}*/
	}

	/**
	 * Called when we have received a packet in the correct order.
	 * 
	 * @param from
	 *            The address of the sender
	 * @param i
	 *            The number of the packet received.
	 */
	public void correctReceive(int from, int i) {
		logOutput("Correctly Received " + i + " from " + from);
		receivedNums.put(from, i);
	}

	/**
	 * Called when we have received a packet from the underlying layer out of
	 * order.
	 * 
	 * @param from
	 *            The address of the sender
	 * @param i
	 *            The number of the packet received.
	 */
	public void failure(int from, int i) {
		logError("FAILURE OF THE RIO MESSAGE LAYER!!  Received " + i
				+ " instead of " + receivedNums.get(from) + " from " + from);
		failed = true;
		fail();
	}

        private String[] parseCommand(String mCommand) {
            String[] commandArr = mCommand.split("\\s+");
            String[] resultStr = new String[3];

            if (commandArr.length != 2) {
                System.out.println("Command Parse Error: Lenght not equal to 2");
                resultStr[2] = "false";
                return resultStr;
            }

            try {
                Integer.parseInt(commandArr[1]);
            } catch (Exception e) {
                System.out.println("Node addr not an integer");
                resultStr[2] = "false";
                return resultStr;
            }

/*              
            switch(commandArr[0]) {
                case "propose":
                break;
                default: 
                    System.out.println("Unknown Command" + commandArr[1]);
                    resultStr[2] = "false";
                    return resultStr;
                break;
            }
*/
            resultStr[0] = commandArr[0];
            resultStr[1] = commandArr[1];
            resultStr[2] = "true";
            return resultStr;
        }

	public void logError(String output) {
		log(output, System.err);
	}

	public void logOutput(String output) {
		log(output, System.out);
	}

	public void log(String output, PrintStream stream) {
		stream.println("Node " + addr + ": " + output);
	}
	
	@Override
	public String toString() {
		if (failed) {
			return "FAILED!!!\n" + super.toString();
		} else {
			return super.toString();
		}
	}
}
