package com.atc.simulator.PredictionService.Engine.Algorithms.OpenCL;

import org.jocl.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.jocl.CL.*;
import static org.jocl.CL.clReleaseProgram;

/**
 * @author Luke Frisken
 */
public class OpenCLPredictionAlgorithm {
    private cl_kernel kernel;
    private cl_program program;
    private cl_context context;
    private cl_command_queue commandQueue;
    private cl_mem memObjects[]; //memory objects for the input- and output data

    //position + velocity
    private static final int SRC_ITEM_FLOATS = 1;
    //time + itemID
    private static final int SRC_ITEM_INTS = 1;

    //position
    private static final int DST_ITEM_FLOATS = 1;

    //time
    private static final int DST_ITEM_INTS = 1;

    /**
     * The source code of the OpenCL program to execute
     */
    private static String sourceFilePath = "assets/opencl/test.cl";


    private boolean built;
    private boolean contextCreated;
    private boolean argumentsSet;

    public OpenCLPredictionAlgorithm()
    {
        built = false;
        contextCreated = false;
        argumentsSet = false;
        memObjects = new cl_mem[4];
    }

    public void run()
    {
        // Create input- and output data
        int n = 10;
        float[] srcFloats = new float[n*SRC_ITEM_FLOATS];
        int[] srcInts = new int[n*SRC_ITEM_INTS];
        float[] dstFloats = new float[n*DST_ITEM_FLOATS];
        int[] dstInts = new int[n*DST_ITEM_INTS];

        for (int i=0; i<n; i++)
        {
            srcFloats[i] = i;
            srcInts[i] = i;
        }
        
        createContext(0, CL_DEVICE_TYPE_ALL, 0);
        buildKernel();
        setKernelArguments(n, srcFloats, srcInts);
        executeKernel(n, dstFloats, dstInts);



        // Verify the result
        boolean passed = true;
        final float epsilon = 1e-7f;
        for (int i=0; i<n; i++)
        {
            float x = dstFloats[i];
            float y = srcFloats[i] * srcInts[i];
            boolean epsilonEqual = Math.abs(x - y) <= epsilon * Math.abs(x);
            if (!epsilonEqual)
            {
                passed = false;
                break;
            }
        }

        System.out.println("Test "+(passed?"PASSED":"FAILED"));
        if (n <= 10)
        {
            System.out.println("Result: "+java.util.Arrays.toString(dstFloats));
        }

        release();
    }

    private String loadSource()
    {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(sourceFilePath));
            return new String(encoded, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createContext(int platformIndex,
                              long deviceType,
                              int deviceIndex
                              )
    {

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        commandQueue =
                clCreateCommandQueue(context, device, 0, null);

        contextCreated = true;

    }

    private void buildKernel()
    {
        if(contextCreated) {
            if (!built) {
                // Create the program from the source code
                String sourceCode = loadSource();
                program = clCreateProgramWithSource(context,
                        1, new String[]{sourceCode}, null, null);

                // Build the program
                clBuildProgram(program, 0, null, null, null, null);

                // Create the kernel
                kernel = clCreateKernel(program, "sampleKernel", null);
                built = true;
            }
        } else {
            System.err.println("You need to create the context before building the kernel");
        }

    }

    private void setKernelArguments(int n, float[] srcFloats, int[] srcInts)
    {
        if(contextCreated && built) {
            Pointer srcFloatsPtr = Pointer.to(srcFloats);
            Pointer srcIntsPtr = Pointer.to(srcInts);

            // Allocate the memory objects for the input- and output data
            memObjects[0] = clCreateBuffer(context,
                    CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                    Sizeof.cl_float * SRC_ITEM_FLOATS * n, srcFloatsPtr, null);
            memObjects[1] = clCreateBuffer(context,
                    CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                    Sizeof.cl_int * SRC_ITEM_INTS * n, srcIntsPtr, null);
            memObjects[2] = clCreateBuffer(context,
                    CL_MEM_READ_WRITE,
                    Sizeof.cl_float * DST_ITEM_FLOATS * n, null, null);
            memObjects[3] = clCreateBuffer(context,
                    CL_MEM_READ_WRITE,
                    Sizeof.cl_int * DST_ITEM_FLOATS * n, null, null);


            // Set the arguments for the kernel
            clSetKernelArg(kernel, 0,
                    Sizeof.cl_mem, Pointer.to(memObjects[0]));
            clSetKernelArg(kernel, 1,
                    Sizeof.cl_mem, Pointer.to(memObjects[1]));
            clSetKernelArg(kernel, 2,
                    Sizeof.cl_mem, Pointer.to(memObjects[2]));
            clSetKernelArg(kernel, 3,
                    Sizeof.cl_mem, Pointer.to(memObjects[3]));

            argumentsSet = true;
        } else {
            System.err.println(
                    "You need to create the context and build the " +
                    "kernel before setting the kernel arguments");
        }
    }

    private void executeKernel(int n, float[] dstFloats, int[] dstInts)
    {
        if (argumentsSet && contextCreated && built)
        {
            Pointer dstFloatsPtr = Pointer.to(dstFloats);
            Pointer dstIntsPtr = Pointer.to(dstInts);

            // Set the work-item dimensions
            long global_work_size[] = new long[]{n};
            long local_work_size[] = new long[]{1};

            // Execute the kernel
            clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                    global_work_size, local_work_size, 0, null, null);

            // Read the output data
            //TODO: figure out whether these are executing in parallel and if not, whether that would be better
            clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
                    Sizeof.cl_float * DST_ITEM_FLOATS * n, dstFloatsPtr, 0, null, null);
            clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0,
                    Sizeof.cl_int * DST_ITEM_INTS * n, dstIntsPtr, 0, null, null);


        } else {
            System.err.println(
                    "You need to create the context, build the " +
                            "kernel and set the kernel arguments before executing the kernel");
        }


    }

    public boolean isBuilt()
    {
        return built;
    }

    public void release()
    {
        // Release kernel, program, and memory objects

        if(built)
        {
            clReleaseKernel(kernel);
            clReleaseProgram(program);
        }

        if(argumentsSet)
        {
            for (int i=0; i < memObjects.length; i++)
            {
                clReleaseMemObject(memObjects[i]);
            }
        }

        if(contextCreated)
        {
            clReleaseCommandQueue(commandQueue);
            clReleaseContext(context);
        }
    }

}
