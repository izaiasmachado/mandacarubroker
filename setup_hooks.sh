#!/bin/bash

# Copy the hooks from the hooks/ directory to the .git/hooks/ directory
cp hooks/* .git/hooks/

# Make the hooks executable
chmod +x .git/hooks/*