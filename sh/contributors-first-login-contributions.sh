#!/bin/sh
#
# contributors-all-complete.sh
#
# Get login and contributions of the first contributor to a GitHub project.

user=lampepfl
repo=dotty
uri="https://api.github.com/repos/$user/$repo/contributors"

curl -s $uri | jq -c '.[0] | {login: .login, contributions: .contributions}'
