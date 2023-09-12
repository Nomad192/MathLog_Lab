#pragma once

#include <iostream>
#include <vector>
#include <string>
#include <regex>
#include <map>
#include <variant>
#include <iterator>
#include <algorithm>

using namespace std;

struct default_tag {};

using node = string;
using transition = vector<variant<node, regex>>;

template <typename Tag = default_tag>
class rules
{
public:
    map<node, vector<transition>> rules;

private:
    static inline void ltrim(string &s) {
        s.erase(s.begin(), find_if(s.begin(), s.end(), [](unsigned char ch) {
            return !isspace(ch);
        }));
    }

    static inline void rtrim(string &s) {
        s.erase(find_if(s.rbegin(), s.rend(), [](unsigned char ch) {
            return !isspace(ch);
        }).base(), s.end());
    }

    static inline void trim(string &s) {
        rtrim(s);
        ltrim(s);
    }

    static size_t count_nonzero_strings(string str)
    {
        istringstream stream(str);
        size_t counter = 0;
        string line;
        while (getline(stream, line)) {
            trim(line);
            if (!line.empty()) {
                counter++;
            }
        }
        return counter;
    }

    static vector<string> split(string str, char delim)
    {
        vector<string> substrings;
        bool insideQuotes = false;
        string currentSubstring;

        for (char c : str) {
            if (c == '\'') {
                insideQuotes = !insideQuotes;
                currentSubstring += c;
            } else if (c == delim && !insideQuotes) {
                substrings.push_back(currentSubstring);
                currentSubstring.clear();
            } else {
                currentSubstring += c;
            }
        }

        if (!currentSubstring.empty()) {
            substrings.push_back(currentSubstring);
        }

        return substrings;
    }

public:
    bool parse(string str_rules)
    {
        map<node, vector<transition>> new_rules;

        regex pattern(R"(<(\w+)>\s*::=(.+))");

        const vector<smatch> matches{
                sregex_iterator{cbegin(str_rules), cend(str_rules), pattern},
                sregex_iterator{}
        };


        size_t nonzero_strings = count_nonzero_strings(str_rules);
        if(nonzero_strings != matches.size())
            return false;

        regex transition_pattern(R"(<(\w+)>|'([^']+)')");

        for(auto match : matches) {
            if(match.size() != 3)
                return false;

//            cout << "node\t" << match.str(1) << endl;
            string right = match.str(2);
//            cout << "right\t" << right << endl;

            vector<string> str_transitions = split(right, '|');
            vector<transition> transitions;

            for(auto str_transition : str_transitions) {

                transition tr;

                const vector<smatch> transition_matches{
                        sregex_iterator{cbegin(str_transition), cend(str_transition), transition_pattern},
                        sregex_iterator{}
                };
//                cout << "\t" << str_transition << endl;

                for(auto tr_match : transition_matches) {
//                    cout << "\t\t" << tr_match.str(0) << endl;
                    if (tr_match.str(0)[0] == '<')
                    {
//                        cout << "\t\t\t" << tr_match.str(1) << endl;
                        tr.push_back(node(tr_match.str(1)));
                    }
                    else /// == '
                    {
//                        cout << "\t\t\t" << tr_match.str(2) << endl;
                        tr.push_back(regex(tr_match.str(2)));
                    }
                }

                transitions.push_back(tr);
            }
            new_rules[match.str(1)] = transitions;
        }

        rules = new_rules;

        return true;
    }
};
