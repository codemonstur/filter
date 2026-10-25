
# Filter

A command line tool that can parse input line-by-line, filter it, and output transformed line using regular expressions. 

This tool is useful if you have a stream of log output, and you want to see only some of it.

## Usage

    $ filter --help
    [ERROR] Missing argument 'config'
    
    Reads input from stdin, runs the lines through the configured rules and outputs results
    
    Usage: filter [options]
    
    Options:
      -c --config   mandatory value   The rules configuration file
      -h --help     optional  flag

## Example

This command moves the timestamp before the tag:

    cat src/data/stream.txt | java -jar target/filter.jar -c src/data/reorder.yml

