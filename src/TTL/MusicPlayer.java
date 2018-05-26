package TTL;

import com.jcraft.jogg.*;
import com.jcraft.jorbis.*;

import java.io.*;
import javax.sound.sampled.*;

public class MusicPlayer extends Thread {
    private InputStream inputStream = null;

    byte[] buffer = null;
    int bufferSize = 2048;
    int count = 0;
    int index = 0;

    byte[] convertedBuffer;
    int convertedBufferSize;

    private SourceDataLine outputLine = null; // The source data line onto which data can be written.
    private float[][][] pcmInfo; // A three-dimensional an array with PCM information.
    private int[] pcmIndex; // The index for the PCM information.

    // Here are the four required JOgg objects...
    private Packet joggPacket = new Packet();
    private Page joggPage = new Page();
    private StreamState joggStreamState = new StreamState();
    private SyncState joggSyncState = new SyncState();

    // ... followed by the four required JOrbis objects.
    private DspState jorbisDspState = new DspState();
    private Block jorbisBlock = new Block(jorbisDspState);
    private Comment jorbisComment = new Comment();
    private Info jorbisInfo = new Info();

    private boolean paused = false;
    private boolean stopped = false;
    private boolean finished = false;

    public MusicPlayer(String filename)  {
        inputStream = ClassLoader.getSystemResourceAsStream(filename);
    }

    public void run() {
        if (inputStream == null) { // Check that we got an InputStream.
            System.err.println("We don't have an input stream and therefore cannot continue.");
            return;
        }
        initializeJOrbis(); // Initialize JOrbis.
        if(readHeader()) {
            if(initializeSound()) {
                readBody();
            }
        }
        cleanUp(); // Afterwards, we clean up.
    }

    public void pause(boolean paused) {
        this.paused = paused;
    }

    public void cleanup() {
        stopped = true;
    }

    public boolean finished() {
        return finished;
    }

    private void initializeJOrbis() {
        joggSyncState.init(); // Initialize SyncState
        joggSyncState.buffer(bufferSize); // Prepare the to SyncState internal buffer
        buffer = joggSyncState.data;
    }

    private boolean readHeader() {
        boolean needMoreData = true;
        int packet = 1;
        while (needMoreData) {
            try {
                count = inputStream.read(buffer, index, bufferSize);
            } catch(IOException exception) {
                System.err.println("Could not read from the input stream.");
                System.err.println(exception);
            }
            joggSyncState.wrote(count);

            switch (packet) {
                case 1: { // We take out a page.
                    switch (joggSyncState.pageout(joggPage)) {
                        case -1: { // If there is a hole in the data, we must exit.
                            System.err.println("There is a hole in the first packet data.");
                            return false;
                        }
                        case 0: { // If we need more data, we break to get it.
                            break;
                        }
                        case 1: {
                            joggStreamState.init(joggPage.serialno()); // Initializes and resets StreamState.
                            joggStreamState.reset();
                            jorbisInfo.init(); // Initializes the Info and Comment objects.
                            jorbisComment.init();

                            // Check the page (serial number and stuff).
                            if(joggStreamState.pagein(joggPage) == -1) {
                                System.err.println("We got an error while "
                                        + "reading the first header page.");
                                return false;
                            }
                            if(joggStreamState.packetout(joggPacket) != 1) {
                                System.err.println("We got an error while "
                                        + "reading the first header packet.");
                                return false;
                            }
                            if(jorbisInfo.synthesis_headerin(jorbisComment, joggPacket) < 0) {
                                System.err.println("We got an error while "
                                        + "interpreting the first packet. "
                                        + "Apparantly, it's not Vorbis data.");
                                return false;
                            }
                            packet++; // We're done here, let's increment "packet".
                            break;
                        }
                    }
                    if(packet == 1) break;
                }
                case 2:	case 3: {
                    switch(joggSyncState.pageout(joggPage)) { // Try to get a new page again.
                        case -1: { // If there is a hole in the data, we must exit.
                            System.err.println("There is a hole in the second or third packet data.");
                            return false;
                        }
                        case 0: // If we need more data, we break to get it.
                            break;
                        case 1: {
                            joggStreamState.pagein(joggPage); // Share the page with the StreamState object.
                            switch(joggStreamState.packetout(joggPacket)) { // Just like the switch(...packetout...) lines above.
                                case -1: { // If there is a hole in the data, we must exit.
                                    System.err.println("There is a hole in the first packet data.");
                                    return false;
                                }
                                case 0: // If we need more data, we break to get it.
                                    break;
                                case 1: { // We got a packet, let's process it.
                                    jorbisInfo.synthesis_headerin(jorbisComment, joggPacket);
                                    packet++; // Increment packet.
                                    if(packet == 4) {
                                        needMoreData = false;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }

            // We get the new index and an updated buffer.
            index = joggSyncState.buffer(bufferSize);
            buffer = joggSyncState.data;

            if(count == 0 && needMoreData) { // If we need more data but can't get it, the stream doesn't contain enough information.
                System.err.println("Not enough header data was supplied.");
                return false;
            }
        }

        return true;
    }

    private boolean initializeSound() {
        convertedBufferSize = bufferSize * 2; // This buffer is used by the decoding method.
        convertedBuffer = new byte[convertedBufferSize];
        jorbisDspState.synthesis_init(jorbisInfo); // Initializes the DSP synthesis.
        jorbisBlock.init(jorbisDspState); // Make the Block object aware of the DSP.
        int channels = jorbisInfo.channels; // Wee need to know the channels and rate.
        int rate = jorbisInfo.rate;
        AudioFormat audioFormat = new AudioFormat((float) rate, 16, channels, true, false); // Creates an AudioFormat object and a DataLine.Info object.
        DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        if (!AudioSystem.isLineSupported(datalineInfo)) { // Check if the line is supported.
            System.err.println("Audio output line is not supported.");
            return false;
        }
        try {
            outputLine = (SourceDataLine) AudioSystem.getLine(datalineInfo);
            outputLine.open(audioFormat);
        } catch(LineUnavailableException exception) {
            System.out.println("The audio output line could not be opened due to resource restrictions.");
            System.err.println(exception);
            return false;
        } catch(IllegalStateException exception) {
            System.out.println("The audio output line is already open.");
            System.err.println(exception);
            return false;
        } catch(SecurityException exception) {
            System.out.println("The audio output line could not be opened due to security restrictions.");
            System.err.println(exception);
            return false;
        }
        outputLine.start(); // Start it.
        pcmInfo = new float[1][][];
        pcmIndex = new int[jorbisInfo.channels];
        return true;
    }

    private void readBody() {
        boolean needMoreData = true;
        while (needMoreData & !stopped) {
            if (paused) {
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            switch(joggSyncState.pageout(joggPage)) {
                case -1: { // If there is a hole in the data, we just proceed.
                    System.err.println("There is a hole in the data. We proceed.");
                }
                case 0: { // If we need more data, we break to get it.
                    break;
                }
                case 1: { // If we have successfully checked out a page, we continue.
                    joggStreamState.pagein(joggPage); // Give the page to the StreamState object.
                    if(joggPage.granulepos() == 0) { // If granulepos() returns "0", we don't need more data.
                        needMoreData = false;
                        break;
                    }
                    processPackets: while (true) { // Here is where we process the packets.
                        switch(joggStreamState.packetout(joggPacket)) {
                            case -1: // Is it a hole in the data?
                                System.err.println("There is a hole in the data, we continue though.");
                            case 0: // If we need more data, we break to get it.
                                break processPackets;
                            case 1: // If we have the data we need, we decode the packet.
                                decodeCurrentPacket();
                        }
                    }
                    if (joggPage.eos() != 0) needMoreData = false; // If the page is the end-of-stream, we don't need more data.
                }
            }

            if(needMoreData) { // If we need more data...
                index = joggSyncState.buffer(bufferSize); // We get the new index and an updated buffer.
                buffer = joggSyncState.data;
                try { // Read from the InputStream.
                    count = inputStream.read(buffer, index, bufferSize);
                } catch(Exception e) {
                    // System.err.println(e);
                    return;
                }
                joggSyncState.wrote(count); // We let SyncState know how many bytes we read.
                if(count == 0) needMoreData = false; // There's no more data in the stream.
            }
        }
    }

    private void cleanUp() {
        // Clear the necessary JOgg/JOrbis objects.
        joggStreamState.clear();
        jorbisBlock.clear();
        jorbisDspState.clear();
        jorbisInfo.clear();
        joggSyncState.clear();
        finished = true;

        try { // Closes the stream.
            if(inputStream != null) inputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void decodeCurrentPacket() {
        int samples;
        if (jorbisBlock.synthesis(joggPacket) == 0) { // Check that the packet is a audio data packet etc.
            jorbisDspState.synthesis_blockin(jorbisBlock); // Give the block to the DspState object.
        }

        int range;
        while((samples = jorbisDspState.synthesis_pcmout(pcmInfo, pcmIndex)) > 0) {
            range = Math.min(samples, convertedBufferSize); // We need to know for how many samples we are going to process.

            for(int i = 0; i < jorbisInfo.channels; i++) { // For each channel...
                int sampleIndex = i * 2;
                for(int j = 0; j < range; j++) { // For every sample in our range...
                    int value = (int) (pcmInfo[0][i][pcmIndex[i] + j] * 32767); // Get the PCM value for the channel at the correct position.
                    if (value > 32767) { // We make sure our value doesn't exceed or falls below +-32767.
                        value = 32767;
                    } else if (value < -32768) {
                        value = -32768;
                    }
                    if (value < 0) {
                        value = value | 32768;
                    }
                    convertedBuffer[sampleIndex] = (byte) (value);
                    convertedBuffer[sampleIndex + 1] = (byte) (value >>> 8);
                    sampleIndex += 2 * (jorbisInfo.channels);
                }
            }

            outputLine.write(convertedBuffer, 0, 2 * jorbisInfo.channels * range); // Write the buffer to the audio output line.
            jorbisDspState.synthesis_read(range); // Update the DspState object.
        }
    }
}
